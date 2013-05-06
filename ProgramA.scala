import scala.util.control.Breaks._
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.asScalaIterator
import com.sun.jdi.connect._
import com.sun.jdi.event.MethodExitEvent
import com.sun.jdi.request._
import com.sun.jdi.Bootstrap
import com.sun.jdi.StringReference
import com.sun.jdi.ThreadReference
import com.sun.jdi.event.MethodEntryEvent

object ProgramA extends App {
  //Connect to target VM
  var initconn: LaunchingConnector = _
  val vm = getVM(initconn)
  println("Attached to process '" + vm.name + "'")


  //Create Event Requests
  val excludes = Array("java.*", "javax.*", "sun.*", "com.sun.*", "com.apple.*")
  createRequests(excludes);


  //Iterate through Events
  val evtQueue = vm.eventQueue
  vm.resume


  //Find main thread in target VM
  val allThrds = vm.allThreads
  var mainThread: ThreadReference = _
  allThrds.foreach { x =>
    if (x.name == "main")
      mainThread = x
  }


  while (true) {
    val evtSet = evtQueue.remove()
    for (evt <- evtSet.eventIterator) {
      evt match {
        case methodEnterEvt: MethodEntryEvent => 
          try {
            //Locate current stackframe, find get value of 'n' variable
            val frame = mainThread.frame(0)
            val n = methodEnterEvt.method().arguments()(0)
            val argval = frame.getValue(n)
            println("Method Enter Event (arg n): " + argval)
            }
            catch {
              case _: Throwable => println("Method Enter Event: " + methodEnterEvt.method().name)
            }
        case methodExitEvt: MethodExitEvent   => println("Method Exit Event (return value): " + methodExitEvt.returnValue)
        case _                                =>
      }
    }
    evtSet.resume()
  }

  def createRequests(excludes : Array[String]) {
    val evtReqMgr = vm.eventRequestManager
    
    val mthdEnterVal = evtReqMgr.createMethodEntryRequest()
    excludes.foreach { mthdEnterVal.addClassExclusionFilter(_) }
    mthdEnterVal.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD)
    mthdEnterVal.enable()

    val mthdExitVal = evtReqMgr.createMethodExitRequest()
    excludes.foreach { mthdExitVal.addClassExclusionFilter(_) }
    mthdExitVal.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD)
    mthdExitVal.enable()
  }

  def getVM(initconn : LaunchingConnector) = {
    var connector = initconn

    val conns = Bootstrap.virtualMachineManager().allConnectors();
    breakable { 
      for (conn <- conns) { 
      if (conn.name().equals("com.sun.jdi.CommandLineLaunch"))
        {
          connector = conn.asInstanceOf[LaunchingConnector]
          break
        }
      }
    }

    // get connector field for program's main() method
    val connArgs = connector.defaultArguments();
    val mArgs = connArgs.get("main").asInstanceOf[Connector.Argument]
    if (mArgs == null)
     throw new Error("Bad launching connector");
    // concatenate all tracer's input args into a single string
    val sb = new StringBuffer();
    for (i <- 0 to (args.length -1))
     sb.append(args(i) + " ");
    
    mArgs.setValue(sb.toString()); // assign args to main field

    val vm = connector.launch(connArgs)
    vm
  }
}
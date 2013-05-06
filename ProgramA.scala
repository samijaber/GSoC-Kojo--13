import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.asScalaIterator
 
import com.sun.jdi.connect.Connector
import com.sun.jdi.event.MethodExitEvent
import com.sun.jdi.request._
import com.sun.jdi.Bootstrap
import com.sun.jdi.StringReference
 
object ProgramA extends App {
  val debugPort = args(0).toInt
  //val lineNumber = args(1).toInt
  //val varName = args(2)
  val vmMgr = Bootstrap.virtualMachineManager
  val socketConnector = vmMgr.attachingConnectors.find(_.transport.name == "dt_socket") match {
    case Some(c) => c
    case None => throw new Exception("not found : dt_socket")
  }
  val paramsMap = socketConnector.defaultArguments
  val portArg = paramsMap.get("port").asInstanceOf[Connector.IntegerArgument]
  portArg.setValue(debugPort)
  val vm = socketConnector.attach(paramsMap)
  println("Attached to process '" + vm.name + "'")

//  vm.setDebugTrace(VirtualMachine.TRACE_ALL)
  //val evtthrd : EventThread = new EventThread(vm, )

  
  val evtReqMgr = vm.eventRequestManager
  val mthdExVal = evtReqMgr.createMethodExitRequest()
  mthdExVal.setSuspendPolicy(EventRequest.SUSPEND_NONE)
  mthdExVal.enable()

  val evtQueue = vm.eventQueue

  while(true)
  {
    val evtSet = evtQueue.remove()
    for (evt <- evtSet.eventIterator) {
        evt match {
          case method_evt: MethodExitEvent => println("The value is " + method_evt.returnValue())
          case all => println("whatever " + all.request)
          }
      }
    //comment this line out for non-infinite printing
    vm.resume
  }
}
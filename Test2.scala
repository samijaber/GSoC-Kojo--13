import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.asScalaIterator
 
import com.sun.jdi.connect.Connector
import com.sun.jdi.event.BreakpointEvent
import com.sun.jdi.request.BreakpointRequest
import com.sun.jdi.request.EventRequest
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
}
import scala.collection.JavaConversions._
import java.util.Map;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import scala.util.control.Breaks._

object NewProcTest {
  def main(args :Array[String]) {
    /*
    println("Main")
    val WrapperName = Wrapper.getClass
    Proc.spawn(WrapperName, true)
    println("fin")
    */

    //step1: Connect to process
    var vmMgr: VirtualMachineManager = Bootstrap.virtualMachineManager();
    var socketConnector: AttachingConnector = null;
    //var attachingConnectors = List();
    var attachingConnectors = vmMgr.attachingConnectors();
    breakable {
      attachingConnectors.toList.foreach { ac =>
        if (ac.transport().name().equals("dt_socket"))
        {
          socketConnector = ac;
          break;
        }
      }
      if (socketConnector != null)
      {
        var paramsMap = socketConnector.defaultArguments();
        var portArg: Connector.IntegerArgument = paramsMap.get("port").asInstanceOf[Connector.IntegerArgument];
        portArg.setValue(Integer.parseInt(args(0) ) );
        var vm: VirtualMachine = socketConnector.attach(paramsMap);
        System.out.println("Attached to process '" + vm.name() + "'");
      }
    }
  }
}

object ProcessSpawner{
  def spawn(WrapperName :Class[_],  redirectStream :Boolean)  {
    val separator = System.getProperty("file.separator")
    val classpath = System.getProperty("java.class.path")
    val path = System.getProperty("java.home") + 
      separator + "bin" + separator + "java"
    val processBuilder = 
            new ProcessBuilder(path, "-cp", 
            classpath, 
            WrapperName.getCanonicalName())
    processBuilder.redirectErrorStream(redirectStream)
    val process = processBuilder.start()
    process.waitFor()

    System.out.println("Fin")
  }
}
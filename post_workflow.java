
package macro;

import java.util.*;

import star.common.*;

public class post_workflow extends StarMacro {

  public void execute() {
	String scriptName = getClass().getSimpleName();
	getSimulation().println("Module_"+scriptName);
	long start = System.currentTimeMillis();
	//
	runScript("Super_Scene_Beta_0424.java");
	//runScript("Super_Scene_Section_Beta_0424.java");

	//
	long stop = System.currentTimeMillis();
	long diffMin = (stop - start) / (1000 * 60);
	long t2 = System.currentTimeMillis();
	getSimulation().println("   All Tasks completed: " + diffMin + " Minutes\n");
	getSimulation().println("Module_"+scriptName + ">>>>>>>>>>>>>done\n");
}

        private void runScript(String script) {
        getSimulation().println("===========================================================");
        getSimulation().println("VSim Script: " + script);
        getSimulation().println("===========================================================");
        long start = System.currentTimeMillis();
        new StarScript(getActiveSimulation(),
                new java.io.File(resolvePath(script)),
                getActiveSimulation().getClass().getClassLoader()).play();
        long stop = System.currentTimeMillis();
        long diffMin = (stop - start) / (1000 * 60);
        long t2 = System.currentTimeMillis();
        getSimulation().println("Tasks_"+ script +"_completed: " + diffMin + " Minutes\n");
        
  }
}

package LR4;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.Behaviour;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import lombok.SneakyThrows;

public class JadeTestingKit {
    private AgentContainer mainContainer;

    public void startJade(){
        Properties props = new Properties();
        props.setProperty("gui", "true");
        props.setProperty("services", "jade.core.messaging.TopicManagementService");
        ProfileImpl p = new ProfileImpl(props);
        Runtime.instance().setCloseVM(true);
        mainContainer = Runtime.instance().createMainContainer(p);

    }

    @SneakyThrows
    public void createAgent(String agentName, Behaviour ... behs){
        AgentController newAgent = mainContainer.createNewAgent(agentName, MockAgent.class.getName(), behs);
        newAgent.start();
    }

}

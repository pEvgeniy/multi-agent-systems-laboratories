package LR3.CFG;


import LR3.CFG.CFGClass;
import LR3.CFG.WorkWithConfigGFiles;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class xml {

    public static CFGClass parser(String name) {

        CFGClass cfg = null;
        {
            try {
                JAXBContext context = JAXBContext.newInstance(CFGClass.class);
                Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
                cfg = (CFGClass) jaxbUnmarshaller.unmarshal(new File("C:/Users/Евгений/IdeaProjects/JadeLabs/src/main/java/LR3/XMLFiles/" + name + ".xml"));
//                System.out.println(cfg.toString());
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }

        CFGClass cfgClass = WorkWithConfigGFiles.unMarshalAny(CFGClass.class, "C:/Users/Евгений/IdeaProjects/JadeLabs/src/main/java/LR3/XMLFiles/" + name + ".xml");
//        System.out.println(cfgClass.toString());

        return cfg;
    }

}
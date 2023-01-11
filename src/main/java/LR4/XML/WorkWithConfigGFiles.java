package LR4.XML;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class WorkWithConfigGFiles {

    public static <T> T unMarshalAny(Class<T> clazz,String outPutFileName){
        T object=null;
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Object obj = jaxbUnmarshaller.unmarshal( new File(outPutFileName));
            try {
                object = (T) obj;
            }
            catch (ClassCastException cce) {
                object = null;
                cce.printStackTrace();
            }
        } catch (JAXBException e ) {
            e.printStackTrace();
        }
        return  object;
    }
}

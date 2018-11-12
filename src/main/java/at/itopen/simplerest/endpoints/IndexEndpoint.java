/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.path.RestEndpoint;
import java.util.Map;


/**
 *
 * @author roland
 */
public class IndexEndpoint extends GetEndpoint {

    
    public class IndexData {
        String programmName;
        String apiVersion;
        String maintainer;
        String email;

        public IndexData(String programmName, String apiVersion, String maintainer, String email) {
            this.programmName = programmName;
            this.apiVersion = apiVersion;
            this.maintainer = maintainer;
            this.email = email;
        }

        public String getApiVersion() {
            return apiVersion;
        }

        public String getEmail() {
            return email;
        }

        public String getMaintainer() {
            return maintainer;
        }

        public String getProgrammName() {
            return programmName;
        }
        
        
        
    }
        
    private IndexData data;
    
    /**
     *
     * @param programmName
     * @param apiVersion
     * @param maintainer
     * @param email
     */
    public IndexEndpoint(String programmName, String apiVersion, String maintainer, String email) {
        super("INDEX");
        data=new IndexData(programmName, apiVersion, maintainer, email);
    }
    
    /**
     *
     * @param conversion
     * @param UrlParameter
     */
    @Override
    public void Call(Conversion conversion, Map<String,String> UrlParameter) {
        conversion.getResponse().setData(data);
        
    }

    public IndexData getData() {
        return data;
    }

    @Override
    public String getEndpointName() {
        return super.getEndpointName(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}

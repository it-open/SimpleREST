/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints;

import at.itopen.simplerest.conversion.Conversion;
import java.util.Map;

/**
 *
 * @author roland
 */
public class IndexEndpoint extends GetEndpoint {

    /**
     *
     */
    public class IndexData {

        String programmName;
        String apiVersion;
        String maintainer;
        String email;

        /**
         *
         * @param programmName
         * @param apiVersion
         * @param maintainer
         * @param email
         */
        public IndexData(String programmName, String apiVersion, String maintainer, String email) {
            this.programmName = programmName;
            this.apiVersion = apiVersion;
            this.maintainer = maintainer;
            this.email = email;
        }

        /**
         *
         * @return
         */
        public String getApiVersion() {
            return apiVersion;
        }

        /**
         *
         * @return
         */
        public String getEmail() {
            return email;
        }

        /**
         *
         * @return
         */
        public String getMaintainer() {
            return maintainer;
        }

        /**
         *
         * @return
         */
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
        data = new IndexData(programmName, apiVersion, maintainer, email);
    }

    /**
     *
     * @param conversion
     * @param urlParameter
     */
    @Override
    public void call(Conversion conversion, Map<String, String> urlParameter) {
        conversion.getResponse().setData(data);

    }

    /**
     *
     * @return
     */
    public IndexData getData() {
        return data;
    }

}

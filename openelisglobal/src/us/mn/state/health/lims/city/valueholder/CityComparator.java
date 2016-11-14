/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/ 
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*/
package us.mn.state.health.lims.city.valueholder;

import java.util.Comparator;


/**
 * @author markaae.fr
 *
 */
public class CityComparator implements Comparable<City> {
   String name;
   
   // You can put the default sorting capability here
   public int compareTo(City that) {
      return this.name.compareTo(that.getName());
   }
   
   public static final Comparator<City> NAME_COMPARATOR =
     new Comparator<City>() {
      public int compare(City a, City b) {
    	  City c_a = (City)a;
    	  City c_b = (City)b;
 
         return ((c_a.getId().toLowerCase()).compareTo((c_b.getId().toLowerCase())));

      }
   };
   
   /**
    *  The CITY_LOCALABBREV_NUMERIC_COMPARATOR claims to compare by local_abbreviation, but it actually looks at ID (and its used). This one does it by the city.localAbbreviation field. 
    */
   public static final Comparator<City> CITY_LOCALABBREV_NUMERIC_COMPARATOR =
       new Comparator<City>() {
           public int compare(City o1, City o2) {
               try {
                   return o1.getLocalAbbreviation().compareTo(o2.getLocalAbbreviation());       
               } catch (NumberFormatException nfe) {
                   return o1.getLocalAbbreviation().compareTo(o2.getLocalAbbreviation());
               }
           }
       };

   /**
    *  The CITY_NAME_COMPARATOR claims to compare by name, but it actually looks at ID (and its used). This one does it by the CityName field. 
    */
   public static final Comparator<City> CITY_NAME_COMPARATOR =
       new Comparator<City>() {
           public int compare(City o1, City o2) {
               return o1.getCityName().compareTo(o2.getCityName());            
           }
       };
    
}

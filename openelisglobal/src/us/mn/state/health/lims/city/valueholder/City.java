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

import us.mn.state.health.lims.common.valueholder.EnumValueItemImpl;
import us.mn.state.health.lims.common.valueholder.SimpleBaseEntity;
import us.mn.state.health.lims.common.valueholder.ValueHolder;
import us.mn.state.health.lims.common.valueholder.ValueHolderInterface;

/**
 * @author markaae.fr
 *
 */
public class City extends EnumValueItemImpl implements SimpleBaseEntity<String>{
    private static final long serialVersionUID = 1L;

    private String id;
	private String cityName;
	private String localAbbreviation;
	private String isActive;
	private String cityId;
	private String selectedCityId;
	private ValueHolderInterface city;
	
	public class ComparatorLocalizedName implements Comparator<City> {
        public int compare(City o1, City o2) {
            return o1.getLocalizedName().compareTo(o2.getDefaultLocalizedName());
        }
    }

	public City() {
		super();
		this.city = new ValueHolder();
	}
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	
	public String getSelectedCityId() {
		return selectedCityId;
	}
	public void setSelectedCityId(String selectedCityId) {
		this.selectedCityId = selectedCityId;
	}
	
	public String getLocalAbbreviation() {
		return localAbbreviation;
	}
	public void setLocalAbbreviation(String localAbbreviation) {
		this.localAbbreviation = localAbbreviation;
	}
	
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public City getCity() {
		return (City) this.city.getValue();
	}
	public void setCity(City city) {
		this.city.setValue(city);
	}
	
	public String getIsActive() {
		return this.isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	
	@Override
	public String toString() {
		return "City [id=" + id + ", localAbbreviation="
				+ localAbbreviation + ", cityName=" + cityName + "]";
	}
	
}

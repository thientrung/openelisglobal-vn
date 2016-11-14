/*
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
 * Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
 */

package us.mn.state.health.lims.common.services;

import org.apache.commons.validator.GenericValidator;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.LocaleChangeListener;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.localization.dao.LocalizationDAO;
import us.mn.state.health.lims.localization.daoimpl.LocalizationDAOImpl;
import us.mn.state.health.lims.localization.valueholder.Localization;

/**
 */
public class LocalizationService implements LocaleChangeListener{

    private static final LocalizationService INSTANCE = new LocalizationService(null);
    private static String LANGUAGE_LOCALE = ConfigurationProperties.getInstance().getPropertyValue( ConfigurationProperties.Property.DEFAULT_LANG_LOCALE);
    private static LocalizationDAO localizationDAO = new LocalizationDAOImpl();
    private Localization localization;

    static{
        SystemConfiguration.getInstance().addLocalChangeListener( INSTANCE );
    }

    public LocalizationService( String id){
        if( !GenericValidator.isBlankOrNull( id )){
            localization = localizationDAO.getLocalizationById( id );
        }
    }

    @Override
    public void localeChanged( String locale ){
        LANGUAGE_LOCALE = locale;
    }

    public static String getLocalizedValueById( String id){
        Localization localization = localizationDAO.getLocalizationById( id );

        if( localization == null){
            return "";
        }
        if( LANGUAGE_LOCALE.equals( ConfigurationProperties.LOCALE.FRENCH.getRepresentation() )){
            return localization.getFrench();
        } else if (LANGUAGE_LOCALE.equals( ConfigurationProperties.LOCALE.VIETNAMESE.getRepresentation())) {
        	return localization.getVietnamese();
        }else{
            return localization.getEnglish();
        }
    }

    /**
     * Checks to see if localization is needed, if so it does the update.
     * @param french The french localization
     * @param english The english localization
     * @param vietnamese The vietnamese localization
     * @return true if the object can be found and an update is needed.  False if the id the service was created with is
     * not valid or any language value is empty or null or all language values match what is already in the object
     */
    public boolean updateLocalizationIfNeeded( String english, String french, String vietnamese ){
        if( localization == null || GenericValidator.isBlankOrNull( french ) || GenericValidator.isBlankOrNull( english ) || GenericValidator.isBlankOrNull( vietnamese ) ){
            return false;
        }

        if( localization == null){
            return false;
        }

        if( english.equals( localization.getEnglish() ) && french.equals( localization.getFrench() ) && vietnamese.equals( localization.getVietnamese() ) ){
            return false;
        }

        localization.setEnglish( english );
        localization.setFrench( french );
        localization.setVietnamese( vietnamese );
        return true;
    }

    public Localization getLocalization(){
        return localization;
    }

    public void setCurrentUserId( String currentUserId ){
        if( localization != null ){
            localization.setSysUserId( currentUserId );
        }
    }
}

package eu.europeana.enrichment.web.model;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import eu.europeana.enrichment.model.impl.Keyword;
import eu.europeana.enrichment.model.impl.KeywordWikidataEntity;

public class KeywordUtils {

    public static final String LANG_EN = "en";
    
    public static KeywordView createView(Keyword keyword) {
        try {
        KeywordView view = new KeywordView();
        view.setId(keyword.getPropertyId());
        view.setLang(keyword.getDetectedOriginalLanguage());
        view.setKeyword(keyword.getValue());
        view.setKeywordEn(keyword.getTranslatedValue());
        
        if(keyword.getPreferredWikidataId()!= null && keyword.getPrefferedWikidataEntity() != null) {
            view.setPrefWkdId(keyword.getPreferredWikidataId());
            view.setPrefLabelEn(
                    getLabel(keyword.getPrefferedWikidataEntity().getPrefLabel(), LANG_EN));
            view.setWikidataTypes(getWikidataTypes(keyword.getPrefferedWikidataEntity()));
            view.setType(getWikidataTypes(keyword.getPrefferedWikidataEntity()));
//            view.setDbpId(getDbpediaId(keyword));
//            view.setDbpWkdId(getDbpWkdId(keyword));
//            view.setPosition(getPosition(keyword));
//            view.setWkdIds(getWkdIds(keyword));
//            view.setWkdAltLabelIds(getWkdAltLabelIds(keyword));            
        }
        
        
        return view;
        } catch (Throwable th) {
            th.printStackTrace();
            throw th;
        }
    }



//    private static String getWkdAltLabelIds(Keyword keyword) {
//        return StringUtils.join(keyword.getWikidataLabelAltLabelMatchIds(), ", ");
//    }
//
//
//
//    private static String getWkdIds(Keyword keyword) {
//        return StringUtils.join(keyword.getPreferredWikidataIds(), ", ");
//    }



    private static String getWikidataTypes(KeywordWikidataEntity entity) {
        if(entity.getWikidataType() == null) {
            return null;
        }
        return StringUtils.join(entity.getWikidataType().values(), ", ");  
    }



//    private static int getPosition(Keyword keyword) {
//        if(keyword.getKeywordPositionEntities() == null) {
//            return -1;
//        }
//        return keyword.getKeywordPositionEntities().get(0).getOffsetsTranslatedText().get(0);
//    }
//
//
//
//    private static String getDbpWkdId(Keyword keyword) {
//        if(keyword.getDbpediaWikidataIds() == null) {
//            return null;
//        }
//        return keyword.getDbpediaWikidataIds().get(0);        
//    }
//
//    private static String getDbpediaId(Keyword keyword) {
//        if(keyword.getDBpediaIds() == null) {
//            return null;
//        }
//        return keyword.getDBpediaIds().get(0);
//    }


    private static String getLabel(Map<String, String> languageMap, String lang) {
        if(lang == null || languageMap == null) {
            return null;
        }
            
        if(languageMap.containsKey(lang)) {
            return languageMap.get(lang);
        }
        return null;
    }
    
//    private static String getLabel(Keyword keyword, String lang) {
//        if(lang == null) {
//            return null;
//        }
//            
//        if(keyword.getPrefLabel() != null && keyword.getPrefLabel().containsKey(lang)) {
//            return keyword.getPrefLabel().get(lang);
//        }
//        return null;
//    }


}

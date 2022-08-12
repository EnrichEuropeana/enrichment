package eu.europeana.enrichment.web.model;

import org.apache.commons.lang3.StringUtils;

import eu.europeana.enrichment.model.impl.KeywordNamedEntity;

public class KeywordUtils {

    public static final String LANG_EN = "en";
    
    public static KeywordView createView(KeywordNamedEntity kne) {
        try {
        KeywordView view = new KeywordView();
        view.setId(kne.get_id());
        String origLang = getOriginalLang(kne);
        view.setLang(origLang);
        view.setKeyword(getLabel(kne, origLang));
        view.setKeywordEn(getLabel(kne, LANG_EN));
        view.setPrefLabelEn(kne.getLabel());
        view.setDbpId(getDbpediaId(kne));
        view.setDbpWkdId(getDbpWkdId(kne));
        view.setPrefWkdId(kne.getPreferedWikidataId());
        view.setPosition(getPosition(kne));
        view.setType(kne.getType());
        view.setWikidataTypes(getWikidataTypes(kne));
        view.setWkdIds(getWkdIds(kne));
        view.setWkdAltLabelIds(getWkdAltLabelIds(kne));
        
        return view;
        } catch (Throwable th) {
            th.printStackTrace();
            throw th;
        }
    }



    private static String getWkdAltLabelIds(KeywordNamedEntity kne) {
        return StringUtils.join(kne.getWikidataLabelAltLabelMatchIds(), ", ");
    }



    private static String getWkdIds(KeywordNamedEntity kne) {
        return StringUtils.join(kne.getPreferredWikidataIds(), ", ");
    }



    private static String getWikidataTypes(KeywordNamedEntity kne) {
        if(kne.getWikidataType() == null) {
            return null;
        }
        return StringUtils.join(kne.getWikidataType().values(), ", ");  
    }



    private static int getPosition(KeywordNamedEntity kne) {
        if(kne.getKeywordPositionEntities() == null) {
            return -1;
        }
        return kne.getKeywordPositionEntities().get(0).getOffsetsTranslatedText().get(0);
    }



    private static String getDbpWkdId(KeywordNamedEntity kne) {
        if(kne.getDbpediaWikidataIds() == null) {
            return null;
        }
        return kne.getDbpediaWikidataIds().get(0);        
    }

    private static String getDbpediaId(KeywordNamedEntity kne) {
        if(kne.getDBpediaIds() == null) {
            return null;
        }
        return kne.getDBpediaIds().get(0);
    }



    private static String getLabel(KeywordNamedEntity kne, String lang) {
        if(lang == null) {
            return null;
        }
            
        if(kne.getPrefLabel() != null && kne.getPrefLabel().containsKey(lang)) {
            return kne.getPrefLabel().get(lang);
        }
        return null;
    }

    private static String getOriginalLang(KeywordNamedEntity kne) {
        if(kne.getPrefLabel() == null) {
            return null;
        }                 
        if(kne.getPrefLabel().size() == 1) {
            return LANG_EN;
        }else {
            for (String key : kne.getPrefLabel().keySet()) {
                if(!LANG_EN.equals(key)) {
                    return key;
                }
            }
        }
        return null;
    }

}

package eu.europeana.enrichment.web.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import eu.europeana.enrichment.definitions.model.impl.Keyword;
import eu.europeana.enrichment.definitions.model.impl.KeywordWikidataEntity;

public class KeywordUtils {

    public static final String LANG_EN = "en";
    
    public static KeywordView createView(Keyword keyword) {
        try {
        KeywordView view = new KeywordView();
        view.setObjectId(keyword.get_id().toHexString());
        view.setPropertyId(keyword.getPropertyId());
        view.setDetectedOriginalLanguage(keyword.getDetectedOriginalLanguage());
        view.setValue(keyword.getValue());
        view.setTranslatedValue(keyword.getTranslatedValue());
        view.setStatus(keyword.getStatus());
                
        if(keyword.getPreferredWikidataId()!= null && keyword.getPrefferedWikidataEntity() != null) {
            view.setPreferredWikidataId(keyword.getPreferredWikidataId());
            view.setPrefLabelEn(
                    getLabel(keyword.getPrefferedWikidataEntity().getPrefLabel(), LANG_EN));
            view.setWikidataTypes(getWikidataTypes(keyword.getPrefferedWikidataEntity()));
            view.setType(getWikidataTypes(keyword.getPrefferedWikidataEntity()));
            view.setTpItemIds(top5(keyword.getTpItemIds()));
            view.setDbpediaWikidataIds(keyword.getDbpediaWikidataIds());
            view.setWikidataLabelAltLabelMatchIds(keyword.getWikidataLabelAltLabelMatchIds());
            view.setApprovedWikidataId(keyword.getApprovedWikidataId());
        }
        
        
        return view;
        } catch (Throwable th) {
            th.printStackTrace();
            throw th;
        }
    }



    private static List<Long> top5(List<Long> tpItemIds) {
    	if(tpItemIds==null) {
    		return Collections.emptyList();
    	}
        int count = Math.min(5, tpItemIds.size());
        return tpItemIds.subList(0, count);
    }

    private static String getWikidataTypes(KeywordWikidataEntity entity) {
        if(entity.getWikidataType() == null) {
            return null;
        }
        return StringUtils.join(entity.getWikidataType().values(), ", ");  
    }

    private static String getLabel(Map<String, String> languageMap, String lang) {
        if(lang == null || languageMap == null) {
            return null;
        }
            
        if(languageMap.containsKey(lang)) {
            return languageMap.get(lang);
        }
        return null;
    }
    
}

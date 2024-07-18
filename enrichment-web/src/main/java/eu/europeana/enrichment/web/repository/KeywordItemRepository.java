package eu.europeana.enrichment.web.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;

import eu.europeana.enrichment.web.model.KeywordItemView;

@Repository()
public interface KeywordItemRepository extends JpaRepository<KeywordItemView, Long>{

    @Query("SELECT keywordItem FROM KeywordItemView keywordItem WHERE keywordItem.keywordId = :keywordId")
    Page<KeywordItemView> retrieveByKeywordId(@Param("keywordId") long keywordId, Pageable pageable);

    @Query("SELECT keywordItem FROM KeywordItemView keywordItem WHERE keywordItem.keywordId = :keywordId")
    List<KeywordItemView> retrieveByKeywordId(@Param("keywordId") long keywordId);

    
    @Query("SELECT count(keywordItem) FROM KeywordItemView keywordItem WHERE keywordItem.keywordId = :keywordId")
    int countByKeywordId(@Param("keywordId") long keywordId);


}

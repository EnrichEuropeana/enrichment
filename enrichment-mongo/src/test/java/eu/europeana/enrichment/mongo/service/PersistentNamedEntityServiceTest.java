package eu.europeana.enrichment.mongo.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.mongo.dao.NamedEntityDao;
import eu.europeana.enrichment.mongo.dao.NamedEntityDaoImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/mongo-config.xml"})
public class PersistentNamedEntityServiceTest {

	@Resource(name = "namedEntityDao")
	NamedEntityDaoImpl namedEntityDao;
	
	@Test
	public void test() throws Exception {
		/*PersistentNamedEntity entity = new PersistentNamedEntity("Person_1");
		//controller.saveNamedEntity(entity);
		namedEntityDao.saveNamedEntity(entity);*/
		
		fail("Not yet implemented");
	}

}

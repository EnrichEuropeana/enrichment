package eu.europeana.enrichment.ner.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.MentionsAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.enumeration.NERStanfordClassification;
import eu.europeana.enrichment.ner.exception.NERAnnotateException;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;
import eu.fbk.dh.tint.runner.TintPipeline;
import eu.fbk.dh.tint.runner.TintRunner;

/*
 * TODO: improve the code to have one same class for all Stanford models 
 */
public class NERStanfordServiceItalianImpl implements NERService{

	private CRFClassifier<CoreLabel> classifier;
	private TintPipeline pipeline; 
	/*
	 * This class constructor loads a model for the Stanford named
	 * entity recognition and classification
	 */
	public NERStanfordServiceItalianImpl() {

		// Initialize the Tint pipeline
		pipeline = new TintPipeline();

		// Load the default properties
		// see https://github.com/dhfbk/tint/blob/master/tint-runner/src/main/resources/default-config.properties
		try {
			pipeline.loadDefaultProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		// Add a custom property
		// pipeline.setProperty("my_property", "my_value");
		pipeline.setProperty("ner.applyFineGrained", "0");
		pipeline.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");

		// Load the models
		pipeline.load();
	}
		
	@Override
	public TreeMap<String, List<List<String>>> identifyNER(String text) throws NERAnnotateException {
		
		
		  // Use for example a text in a String text = "I topi non avevano nipoti. Il mio nome è Nicolo Rossi. Il mio nome è Massimo Pietro."; 
		text =
		  "I topi non avevano nipoti. Il mio nome è Nicolo Rossi Massimo Pietro. L'Italia, ufficialmente Repubblica italiana, è uno Stato (301.338 km², 60.303.800 abitanti al 31 ottobre 2009) dell'Europa meridionale il cui territorio coincide in gran parte con l'omonima regione geografica.\r\n"
		  + "\r\n" +
		  "Chiamata anche con le antonomasie di \"lo Stivale\" per la sua forma a stivale e \"bel paese\" in ragione del suo clima e delle sue bellezze naturali ed artistiche, geograficamente l'Italia è costituita da tre parti: una continentale, delineata a nord dalle Alpi e a sud dalla linea convenzionale che congiunge La Spezia con Rimini, una peninsulare, che si allunga nel Mediterraneo in direzione nord ovest - sud est, ed una insulare, che comprende le due maggiori isole del Mediterraneo, la Sardegna e la Sicilia presso la quale, in corrispondenza dell'isola di Pantelleria, si ha la minima distanza dall'Africa, distante circa 70 chilometri. I confini territoriali si estendono complessivamente per 1.800 chilometri, mentre lo sviluppo costiero raggiunge i 7.500 chilometri.\r\n"
		  + "\r\n" +
		  "Confina ad ovest con la Francia, a nord con la Svizzera e l'Austria e ad est con la Slovenia. I microstati San Marino e Città del Vaticano sono enclavi interamente comprese nel suo territorio, mentre il comune di Campione d'Italia costituisce una exclave situata nella regione italofona del Canton Ticino in Svizzera.\r\n"
		  + "\r\n" +
		  "L'Italia è una repubblica parlamentare; l'attuale presidente della Repubblica è Giorgio Napolitano e il presidente del Consiglio è Silvio Berlusconi. La lingua ufficiale è l'italiano. La Costituzione prevede anche il bilinguismo con altre lingue in alcune province e tutela alcune minoranze linguistiche presenti nel territorio nazionale.\r\n"
		  + "\r\n" +
		  "Dal 1871 la capitale è la città di Roma, \"erede\" di Firenze, sede (per cinque anni) degli organi statutari che sostituì Torino nel 1865; durante la seconda guerra mondiale, per alcuni mesi (settembre 1943-febbraio 1944) la capitale fu trasferita a Brindisi e, successivamente, a Salerno.\r\n"
		  + "\r\n" +
		  "Lo Stato indipendente ed unitario, nato nel 1861 come Regno d'Italia sotto la dinastia di casa Savoia, aveva un'estensione territoriale che non comprendeva ancora Roma e gran parte dell'attuale Lazio, che formavano lo Stato Pontificio (incorporato il 20 settembre 1870), il Veneto e il Friuli che erano parte dell'Impero d'Austria (acquisiti nel 1866), la Venezia Giulia ed il Trentino-Alto Adige anch'essi sotto dominio asburgico (annessi a seguito della prima guerra mondiale); ha assunto l'attuale forma repubblicana il 18 giugno 1946 a seguito del risultato del referendum del 2 giugno indetto per stabilire la forma istituzionale dello Stato dopo la fine della seconda guerra mondiale. Successivamente, l'Assemblea costituente eletta lo stesso giorno del referendum elaborò la Costituzione che, entrata in vigore il 1º gennaio 1948, dà alla Repubblica un carattere parlamentare.\r\n"
		  + "\r\n" +
		  "Avendo sottoscritto nel 1951 il Trattato di Parigi che istituiva la Comunità europea del carbone e dell'acciaio (CECA), l'Italia è uno dei sei membri fondatori dell'Unione europea ed ha partecipato a tutti i principali trattati di unificazione europea, compreso l'ingresso nell'area dell'Euro nel 1999. Inoltre è membro fondatore della NATO, del Consiglio d'Europa, dell'OCSE, aderisce all'ONU, all'Unione Europea Occidentale e fa parte del G7 e del G8.";
		 
		// Get the original Annotation (Stanford CoreNLP)
		Annotation stanfordAnnotation = pipeline.runRaw(text);

		TreeMap<String, List<List<String>>> map = processStanfordNLPAnnotation(stanfordAnnotation);
		
		// **or**
		// Get the JSON
		// (optionally getting the original Stanford CoreNLP Annotation as return value)
		/*
		 * InputStream stream = new
		 * ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)); try { Annotation
		 * annotation = pipeline.run(stream, System.out, TintRunner.OutputFormat.JSON);
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */	
		
		return map;
	}
	
	/*
	 * This methods combines words and creates a TreeMap based on the classification
	 * 
	 * @param result 					contains Stanford NLP Annotation to be processed
	 * @return 							a TreeMap with all relevant words
	 * @throws 							NERAnnotateException
	 */
	//TODO: check where exception could appear
	private TreeMap<String, List<List<String>>> processStanfordNLPAnnotation(Annotation result) throws NERAnnotateException{

		TreeMap<String, List<List<String>>> map = new TreeMap<String, List<List<String>>>();
			
		List<CoreMap> labelAnnotations = (List<CoreMap>) result.get(MentionsAnnotation.class);
		
		
		for (int i=0;i<labelAnnotations.size();i++) {
			
			Annotation entity = (Annotation) labelAnnotations.get(i);

			/*
			 * The end of the offset can be taken using: entity.get(CharacterOffsetEndAnnotation.class)
			 */
			int wordOffset=entity.get(CharacterOffsetBeginAnnotation.class);			
			String word = entity.get(TextAnnotation.class);
			String category = entity.get(NamedEntityTagAnnotation.class);
			
			/*
			 * check the label category
			 */
			if(NERStanfordClassification.isAgent(category))
				category = NERClassification.AGENT.toString();
			else if(NERStanfordClassification.isPlace(category))
				category = NERClassification.PLACE.toString();
			else if(NERStanfordClassification.isOrganization(category))
				category = NERClassification.ORGANIZATION.toString();
			else if(NERStanfordClassification.isMisc(category))
				category = NERClassification.MISC.toString();
						
			
			if (!"O".equals(category)) {				
				
				List<String> wordWithPosition = new ArrayList<String>();
				wordWithPosition.add(word);
				wordWithPosition.add(String.valueOf(wordOffset));						
				
				if (map.containsKey(category)) {
					// key is already their just insert in the list {word,position}
					map.get(category).add(wordWithPosition);
				} else {
					List<List<String>> temp = new ArrayList<List<String>>();					
					temp.add(wordWithPosition);
					map.put(category, temp);
				}
				//System.out.println(word + ":" + category);
			}
		}
		return map;
	}
	
}

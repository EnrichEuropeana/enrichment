
import spacy

class MySpacy:
    def __init__(self, model_path):
        # for linux only #'en_core_web_sm'
        # self.nlp_model = spacy.load(r"C:\Users\katicd\Documents\Europeana\Code\Ait\Code\AIT\venv\Lib\site-packages\en_core_web_sm\en_core_web_sm-2.0.0")
        self.nlp_model = spacy.load(model_path)

    def apply_ner(self, text_string):
        ner_result = self.nlp_model(text_string)
        return self.prepare_ner_result(ner_result)

    def prepare_ner_result(self, ner_result):
        named_entities = {}
        for ent in ner_result.ents:
            if hasattr(ent, 'label') and ent.label_ != 'NORP':
                #ent.start_char -> offset
                entity_name = ent.text
                if ent.label_ == 'GPE' or ent.label_ == 'LOC':
                    entity_type = "place"
                elif ent.label_ == "ORG":
                    entity_type = "organization"
                elif ent.label_ == "PERSON":
                    entity_type = "agent"
                elif ent.label_ == "MISC":
                    entity_type = "misc"
                else:
                    entity_type = ent.label_

                if not entity_type in named_entities:
                    named_entities[entity_type] = []
                if not entity_name in named_entities[entity_type]:
                    named_entities[entity_type].append(entity_name)

        return named_entities


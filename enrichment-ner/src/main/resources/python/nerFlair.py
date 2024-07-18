

from flair.data import Sentence
from flair.models import SequenceTagger

#https://github.com/zalandoresearch/flair

class MyFlair:
    def __init__(self):
        # load the NER tagger
        self.tagger = SequenceTagger.load('ner')

    def apply_ner(self, text_string):
        return self.process_text(text_string)

    def process_text(self, text_string):
        sentence = Sentence(text_string)
        # run NER over sentence
        self.tagger.predict(sentence)
        return self.prepare_ner_results(sentence)

    def prepare_ner_results(self, sentence):
        named_entities = {}
        for entity in sentence.get_spans('ner'):
            #entity.start_pos -> offset
            entity_type = entity.tag
            if entity_type == "LOC":
                entity_type = "place"
            elif entity_type == "PER":
                entity_type = "agent"
            elif entity_type == "ORG":
                entity_type = "organization"
            elif entity_type == "MISC":
                entity_type = "misc"
            entity_name = entity.text
            if not entity_type in named_entities:
                named_entities[entity_type] = []
            if not entity_name in named_entities[entity_type]:
                named_entities[entity_type].append(entity_name)
        return named_entities


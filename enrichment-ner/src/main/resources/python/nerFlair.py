

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
            entity_type = entity.tag
            entity_name = entity.text
            if not entity_type in named_entities:
                named_entities[entity_type] = []
            if not entity_name in named_entities[entity_type]:
                named_entities[entity_type].append(entity_name)
        return named_entities


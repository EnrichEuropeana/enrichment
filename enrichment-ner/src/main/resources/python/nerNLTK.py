# -*- coding: utf-8 -*-

import nltk

class MyNLTK:
    def apply_ner(self, text_string):
        return self.default_nltk_classification(text_string)

    def default_nltk_classification(self, text_string):
        # https://www.commonlounge.com/discussion/2662a77ddcde4102a16d5eb6fa2eff1e
        tokenized_doc = nltk.word_tokenize(text_string)
        # tag sentences and use nltk's Name Entity Chunker
        tagged_sentences = nltk.pos_tag(tokenized_doc)
        ne_chunked_sents = nltk.ne_chunk(tagged_sentences)

        named_entities = {}
        #GPE: Geo-political entity
        #GSP: Generalised Schema of Preference ?

        for tagged_tree in ne_chunked_sents:
            if hasattr(tagged_tree, 'label'):
                entity_name = ' '.join(c[0] for c in tagged_tree.leaves())

                if tagged_tree.label() == "GPE" or tagged_tree.label() == "GSP":
                    entity_type = "PLACE"
                else:
                    entity_type = tagged_tree.label()

                if not entity_type in named_entities:
                    named_entities[entity_type] = []
                if not entity_name in named_entities[entity_type]:
                    named_entities[entity_type].append(entity_name)

        return named_entities

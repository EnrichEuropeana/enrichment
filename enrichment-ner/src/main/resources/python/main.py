# coding=utf-8

import sys
import json
import base64
from argparse import ArgumentParser
#from nerSpaCy import MySpacy
from nerNLTK import MyNLTK
from nerFlair import MyFlair

def stringToBase64(s):
    return base64.b64encode(s.encode('utf-8'))

def base64ToString(b):
    return base64.b64decode(b).decode('utf-8')

def main(spaCyModel):
    # tool definition
    nameSpaCy = "spaCy"
#    mySpaCy = MySpacy(spaCyModel)
    nameNLTK = "nltk"
    myNltk = MyNLTK()
    nameFlair = "flair"
    myFlair = MyFlair()

    text = ""
    for line in sys.stdin:
        text += line
        if line.endswith("{end}\n"):
            text = text[:-6]
            break
    text = base64ToString(text)

    javaRequest = json.loads(text)

    result = ""
    if javaRequest["tool"] == nameSpaCy:
        result = mySpaCy.apply_ner(javaRequest["text"])
    elif javaRequest["tool"] == nameNLTK:
        result = myNltk.apply_ner(javaRequest["text"])
    elif javaRequest["tool"] == nameFlair:
        result = myFlair.apply_ner(javaRequest["text"])
    else:
        result = dict(error="Tool unknown!", request=javaRequest)

    print(stringToBase64(json.dumps(result, ensure_ascii=False)).decode("UTF-8"))
    exit(0)

"""
Example:
    Normal: {"text":"Later I met two Romanians, Alexandru Căluşeri and Ioan Costinaşi. ","tool":"flair"}
    Base64 encoded: eyJ0ZXh0IjoiTGF0ZXIgSSBtZXQgdHdvIFJvbWFuaWFucywgQWxleGFuZHJ1IEPEg2x1xZ9lcmkgYW5kIElvYW4gQ29zdGluYcWfaS4gIiwidG9vbCI6ImZsYWlyIn0={end}
"""

if __name__ == "__main__":
    parser = ArgumentParser()
    parser.add_argument("--spaCy", dest="spaCyModel", help="Define location of spaCy model")
    args = parser.parse_args()
    main(args.spaCyModel)

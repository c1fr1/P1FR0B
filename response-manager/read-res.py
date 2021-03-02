from dotabase import *

import shutil
import os

def response_string(response) :
    simple_text = response.text_simple
    words = simple_text.split(' ')
    return '-'.join(words).strip('-')

def write_tree(response) :
    path = "responses/"
    for word in response.text_simple.split(' '):
        if (len(word) == 0):
            continue
        path += word + "/"
        if not os.path.isdir(path) :
            os.mkdir(path)
    shutil.copyfile(response.mp3.lstrip("/"), path + response.hero.name + ".mp3")

#def write_tree(response) :
#    write_tree(response.text_simple, response.hero.name, response.mp3)

db = dotabase_session()
if os.path.isdir("responses/") :
    shutil.rmtree("responses/")
os.mkdir("responses/")
for hero in db.query(Hero) :
    print(hero.name)
    #os.mkdir("responses/" + hero.name)
    disallowed = ["ye", "yes", "yup", "no", "oh", "ha", "hah", "uh", "gah", "why", "huh", "almost", "hm", "hmm", "hmmm", "ah", "ahh", "ahhh", "thanks", "thank-you", "hey", "ready", "indeed", "ow", "nope", "sure", "yeah", "oof", "wait", "he", "eh", "yep", "wow", "what", "ooh", "ok", "alright", "next"]
    for response in hero.responses :
        path_head = "responses/" + hero.name + "/"
        if (type(response.text_simple) is not type(None)) :
            rstring = response_string(response)
            if not rstring in disallowed :
                #try : 
                    disallowed.append(rstring)
                    write_tree(response)
                    #shutil.copyfile(response.mp3.lstrip("/"), path_head + rstring + ".mp4")
                #except : 
                #    print(path_head + rstring + ".mp4")
                #    print(response.mp3.lstrip("/"))


#for voice in db.query(Voice) :
#    print(voice.icon)

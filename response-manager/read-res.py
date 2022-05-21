import shutil
from dotabase import *

def response_string(response) :
    simple_text = response.text_simple
    if (simple_text is None) :
        return ""
    words = simple_text.split(' ')
    return '-'.join(words).strip('-')

db = dotabase_session()

if os.path.isdir("responses/") :
    shutil.rmtree("responses/")
os.mkdir("responses/")

file = open("response-data.txt", "w")

for hero in db.query(Hero) :
    id = 0
    os.mkdir("responses/" + hero.name + "/")
    disallowed = ["yes", "yup", "no", "oh", "hah", "uh", "gah", "why", "huh", "almost", "hm", "hmm", "hmmm", "ah", "ahh", "ahhh", "thanks", "thank-you", "hey", "ready", "ow", "nope", "sure", "yeah", "oof", "wait", "he", "eh", "yep", "wow", "what", "ooh", "ok", "alright", "next"]
    for response in hero.responses :
        tex = response.text_simple
        if (tex is None or tex in disallowed) :
            continue
        tex = tex.strip()
        path = "responses/" + hero.name + "/" + str(id) + ".mp3"
        file.write(hero.name + ", " + tex + ", " + str(id) + "\n")
        shutil.copyfile(response.mp3.lstrip("/"), path)
        id = id + 1

file.close()

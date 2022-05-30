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
    for response in hero.responses :
        tex = response.text_simple
        if (tex is None) :
            continue
        tex = tex.strip()
        fromPath = response.mp3.lstrip("/")
        if (os.path.isfile(fromPath)) :
            path = "responses/" + hero.name + "/" + str(id) + ".mp3"
            file.write(hero.name + ", " + tex + ", " + str(id) + "\n")
            shutil.copyfile(fromPath, path)
            id = id + 1
        else :
            print("Ignoring " + fromPath + " because it doesn't exist")

file.close()

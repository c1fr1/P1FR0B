from dotabase import *

db = dotabase_session()
for hero in db.query(Hero) :
    if hero.name == "spectre" :
        for response in hero.responses :
       	    print(hero.name)
            print(response.mp3)

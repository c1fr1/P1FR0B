from dotabase import *

db = dotabase_session()
for hero in db.query(Hero) :
    for response in hero.responses :
        print(response.text)
        print(response.text_simple)
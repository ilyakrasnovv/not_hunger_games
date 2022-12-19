import uvicorn
from fastapi import FastAPI

app = FastAPI()


@app.get('/game_state/players')
def a():
    return [{'username': 'ilyakrasnovv', 'shortcode': 'ILKR', 'score': 228, 'potential': 57, 'leader': 'ilyakrasnovv',
             'admin': False},
            {'username': 'school_1507', 'shortcode': 'SC57', 'score': 57, 'potential': 228, 'leader': 'school_1507',
             'admin': False}]


from pydantic import BaseModel


class Content(BaseModel):
    type: str
    timestamp: int
    data: dict


@app.post('/game_state/event')
def b(token: str, body: Content):
    print(token)
    print(body)


uvicorn.run(app, port=2283)

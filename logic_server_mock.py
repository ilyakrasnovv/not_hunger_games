import uvicorn
from fastapi import FastAPI

app = FastAPI()


@app.get('/game_state/players')
def a():
    return [{'userName': 'ilyakrasnovv', 'shortCode': 'ILKR', 'score': 228, 'potential': 57, 'leader': 'ilyakrasnovv',
             'admin': True},
            {'userName': 'school_1507', 'shortCode': 'SC57', 'score': 57, 'potential': 228, 'leader': 'school_1507',
             'admin': False}]


from pydantic import BaseModel


# @Serializable
# protected data class Content<T>(
# val type: String,
# val timestamp: Long,
# val data: T & Any,
# )

class Content(BaseModel):
    type: str
    timestamp: int
    data: dict


@app.post('/game_state/event')
def b(token: str, body: Content):
    print(token)
    print(body)


# @Serializable
# data class GameStatePlayer(
#     val userName: String,
# val shortCode: String,
# val score: Int,
# val potential: Int,
# val leader: String,
# val admin: Boolean,
# ) {

uvicorn.run(app, port=2283)

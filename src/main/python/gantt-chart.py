import altair as alt
import pandas as pd

alt.renderers.enable('altair_viewer')

source = pd.DataFrame([
    {"task": "A", "start": 1, "end": 3},
    {"task": "B", "start": 3, "end": 8},
    {"task": "C", "start": 8, "end": 10}
])

chart = alt.Chart(source).mark_bar().encode(
    x='start',
    x2='end',
    y='task'
)

chart.show()

import requests
from concurrent.futures import ThreadPoolExecutor

URL = "http://localhost:8080/api/inventory/1/decrease"

def decrease_stock():
    response = requests.patch(
        URL,
        json={"quantity": 1}
    )
    return response.status_code, response.text

with ThreadPoolExecutor(max_workers=100) as executor:
    results = list(executor.map(
        lambda _: decrease_stock(),
        range(2)
    ))

for result in results:
    print(result)
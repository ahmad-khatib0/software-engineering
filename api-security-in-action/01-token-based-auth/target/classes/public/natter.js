const apiUrl = 'https://localhost:4567'

function createSpace(name, owner) {
  let data = { name: name, owner: owner }

  fetch(apiUrl + '/spaces', {
    method: 'POST',
    credentials: 'include',
    // sets the credentials attribute to include, to ensure that HTTP Basic credentials are set on the
    // request; otherwise, they would not be, and the request would fail to authenticate.
    body: JSON.stringify(data),
    headers: {
      'Content-Type': 'application/json',
    },
  })
    .then((response) => {
      if (response.ok) {
        return response.json()
      } else {
        throw Error(response.statusText)
      }
    })
    .then((json) => console.log('Created space: ', json.name, json.uri))
    .catch((error) => console.error('Error: ', error))
}

window.addEventListener('load', function (_) {
  document.getElementById('createSpace').addEventListener('submit', processFormSubmit)
})

function processFormSubmit(e) {
  e.preventDefault()
  let spaceName = document.getElementById('spaceName').value
  let owner = document.getElementById('owner').value
  createSpace(spaceName, owner)

  return false
}

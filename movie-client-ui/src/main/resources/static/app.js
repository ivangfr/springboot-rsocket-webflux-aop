var stompClient = null;

function connect() {
    const socket = new SockJS('/websocket')
    stompClient = Stomp.over(socket)

    var prevPriceValue = null
    stompClient.connect({},
        function (frame) {
            console.log('Connected: ' + frame)

            stompClient.subscribe('/topic/movies/updates', function (update) {
                const updateJson = JSON.parse(update.body)
                const icon = pickIcon(updateJson.action)
                const row = '<tr><td>'+icon+'</td><td>'+moment(updateJson.timestamp).format('YYYY-MM-DD HH:mm:ss')+'</td><td>'+updateJson.imdb+'</td><td>'+updateJson.payload+'</td></tr>'
                $('#movieUpdates').find('tbody').prepend(row)
            })
        },
        function() {
            console.log('Unable to connect to Websocket!')
        }
    )
}

function pickIcon(action) {
    let iconName = 'bullhorn'
    switch (action) {
        case 'ADDED':
            iconName = 'plus'
            break
        case 'DELETED':
            iconName = 'minus'
            break
        case 'LIKED':
            iconName = 'thumbs up outline'
            break
        case 'DISLIKED':
            iconName = 'thumbs down outline'
            break
    }
    return '<i class="'+iconName+' big icon">'
}

connect()
let stompClient = null;

function connect() {
    const socket = new SockJS('/websocket')
    stompClient = Stomp.over(socket)

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
            showModal($('.modal.alert'), 'WebSocket Disconnected', 'WebSocket is disconnected. Maybe, movie-client-ui is down or restarting')
            $('.connWebSocket').find('i').removeClass('green').addClass('red')
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

function showModal($modal, header, description, fnApprove) {
    $modal.find('.header').text(header)
    $modal.find('.content').text(description)
    $modal.modal({
        onApprove: function() {
            fnApprove && fnApprove()
        }
    }).modal('show')
}

connect()
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
                const row = '<tr><td>'+updateJson.action+'</td><td>'+moment(updateJson.timestamp).format('YYYY-MM-DD HH:mm:ss')+'</td><td>'+updateJson.imdb+'</td><td>'+updateJson.payload+'</td></tr>'
                $('#moviesUpdatesList').find('tbody').prepend(row)
            })
        },
        function() {
            console.log('Unable to connect to Websocket!')
        }
    )
}

connect()
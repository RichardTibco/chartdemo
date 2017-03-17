/**
 * Created by pyang on 15/03/2017.
 */

var stompClient = null;

function paint() {
    d3.json("/data",function(data){
        console.log(data);
        data = data.map(function(d){
            d.count = +d.count;
            var date = new Date(Date.parse(d.created))
            d.created = date.getFullYear()+'-'+(date.getMonth()+1);
            return d
        })

        var target = [];
        var deb = {};
        var ret = _.groupBy(data, function(n){
            return n.created;
        });

        for(var year_month in ret) {
            var name = year_month;
            var value = _.sumBy(ret[year_month], function(o){
                return o.count;
            });
            deb[year_month]=ret[year_month];
            target.push({"count":value, "created":name});

        }

        var width = 800,
            height = 500,
            margin = {left:30, top:30, right:30, bottom:30},
            svg_width = width + margin.left + margin.right,
            svg_height = height + margin.top + margin.bottom;

        var scale = d3.scale.linear()
            .domain([0, d3.max(target, function(d){
                return d.count;
            })])
            .range([height, 0]);

        var scale_x = d3.scale.ordinal()
            .domain(target.map(function(d){return d.created;}))
            .rangeBands([0, width], 0.1);

        var svg = d3.select("#container")
            .append("svg")
            .attr("width", svg_width)
            .attr("height", svg_height);

        var chart = svg.append("g")
            .attr("transform", "translate("+margin.left+","+margin.top+")");

        var x_axis = d3.svg.axis().scale(scale_x),
            y_axis = d3.svg.axis().scale(scale).orient("left");
        chart.append("g")
            .call(x_axis)
            .attr("transform", "translate(0,"+height+")");
        chart.append("g")
            .call(y_axis);

        var bar = chart.selectAll(".bar")
            .data(target)
            .enter()
            .append("g")
            .attr("class","bar")
            .attr("transform", function(d, i) {
                return "translate(" + scale_x(d.created) + ",0)"
            });

        bar.append("rect")
            .attr({
                "y": function(d){
                    return scale(d.count)
                },
                "width":scale_x.rangeBand(),
                "height":function(d) {
                    return height - scale(d.count)
                }

            })
            .style("fill", "steelblue");


        bar.append("text")
            .attr({
                "y":function(d){
                    return scale(d.count);
                },
                "x":scale_x.rangeBand()/2,
                "dy":-5,
                "text-anchor":"middle"

            })
            .text(function(d){
                return d.count;
            })
    });

}


function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            $("#container").empty();

            paint();
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}



$(function () {
    paint();
    connect();
    window.onbeforeunload = function() {
        disconnect();
    }
});



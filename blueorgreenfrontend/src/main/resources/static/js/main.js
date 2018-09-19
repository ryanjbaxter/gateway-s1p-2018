angular.module('demo', [])
    .controller('Color', function($scope, $http) {
        $http.get('/color').
        then(function(response) {
            $scope.color = response.data;
        });
    });
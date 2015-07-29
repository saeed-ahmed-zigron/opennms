(function() {
	'use strict';

	var MODULE_NAME = 'onmsList.minion';

	// $filters that can be used to create human-readable versions of filter values
	angular.module('minionListFilters', [ 'onmsListFilters' ])
	.filter('property', function() {
		return function(input) {
			switch (input) {
			case 'id':
				return 'ID';
			case 'label':
				return 'Label';
			case 'location':
				return 'Location';
			case 'type':
				return 'Type';
			case 'status':
				return 'Status';
			case 'lastUpdated':
				return 'Last updated';
			}
			// If no match, return the input
			return input;
		}
	})
	.filter('value', function($filter) {
		return function(input, property) {
			switch (property) {
			case 'lastUpdated':
				// Return the date in our preferred format
				return $filter('date')(input, 'MMM d, yyyy h:mm:ss a');
			}
			return input;
		}
	});

	// Minion list module
	angular.module(MODULE_NAME, [ 'ngResource', 'onmsList', 'minionListFilters' ])

	/**
	 * OnmsMinion REST $resource
	 */
	.factory('Minions', function($resource, $log, $http, $location) {
		return $resource(BASE_REST_URL + '/minions/:id', { id: '@id' },
			{
				'query': { 
					method: 'GET',
					isArray: true,
					// Append a transformation that will unwrap the item array
					transformResponse: appendTransform($http.defaults.transformResponse, function(data, headers, status) {
						// TODO: Figure out how to handle session timeouts that redirect to 
						// the login screen
						/*
						if (status === 302) {
							$window.location.href = $location.absUrl();
							return [];
						}
						*/
						// Always return the data as an array
						return angular.isArray(data.minion) ? data.minion : [ data.minion ];
					})
				},
				'update': { 
					method: 'PUT'
				}
			}
		);
	})

	/**
	 * Minion list controller
	 */
	.controller('MinionListCtrl', ['$scope', '$location', '$window', '$log', '$filter', 'Minions', function($scope, $location, $window, $log, $filter, Minions) {
		$log.debug('MinionListCtrl initializing...');

		// Set the default sort and set it on $scope.$parent.query
		$scope.$parent.defaults.orderBy = 'label';
		$scope.$parent.query.orderBy = 'label';

		// Reload all resources via REST
		$scope.$parent.refresh = function() {
			// Fetch all of the items
			Minions.query(
				{
					_s: $scope.$parent.query.searchParam, // FIQL search
					limit: $scope.$parent.query.limit,
					offset: $scope.$parent.query.offset,
					orderBy: $scope.$parent.query.orderBy,
					order: $scope.$parent.query.order
				}, 
				function(value, headers) {
					$scope.$parent.items = value;

					var contentRange = parseContentRange(headers('Content-Range'));
					$scope.$parent.query.lastOffset = contentRange.end;
					// Subtract 1 from the value since offsets are zero-based
					$scope.$parent.query.maxOffset = contentRange.total - 1;
					$scope.$parent.setOffset(contentRange.start);
				},
				function(response) {
					switch(response.status) {
					case 404:
						// If we didn't find any elements, then clear the list
						$scope.$parent.items = [];
						$scope.$parent.query.lastOffset = 0;
						$scope.$parent.query.maxOffset = -1;
						$scope.$parent.setOffset(0);
						break;
					case 401:
					case 403:
						// Handle session timeout by reloading page completely
						$window.location.href = $location.absUrl();
						break;
					}
					// TODO: Handle 500 Server Error by executing an undo callback?
				}
			);
		};

		// Save an item by using $resource.$update
		$scope.$parent.update = function(item) {
			var saveMe = Minions.get({id: item.id}, function() {
				saveMe.label = item.label;
				saveMe.location = item.location;

				// TODO
				//saveMe.status = item.status;
				// TODO
				//saveMe.properties = item.properties;

				// Read-only fields
				// saveMe.type = item.type;
				// saveMe.date = item.date;

				saveMe.$update({}, function() {
					// If there's a search in effect, refresh the view
					if ($scope.$parent.query.searchParam !== '') {
						$scope.$parent.refresh();
					}
				});
			}, function(response) {
				$log.debug(response);
			});

		};

		// Refresh the item list;
		$scope.$parent.refresh();

		$log.debug('MinionListCtrl initialized');
	}])

	.run(['$rootScope', '$log', function($rootScope, $log) {
		$log.debug('Finished initializing ' + MODULE_NAME);
	}])

	;

	angular.element(document).ready(function() {
		console.log('Bootstrapping ' + MODULE_NAME);
		angular.bootstrap(document, [MODULE_NAME]);
	});
}());

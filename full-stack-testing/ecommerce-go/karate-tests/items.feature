Feature: Test GET /items from Go eCommerce service

  Scenario: verify GET items endpoint
    Given url baseUrl + '/items'
    When method get
    Then status 200
    And match response contains deep { SKU: '#string', color: '#string', size: '#string' }

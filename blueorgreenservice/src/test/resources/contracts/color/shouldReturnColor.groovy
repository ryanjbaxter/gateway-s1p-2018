package contracts.color

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        url '/'
        headers {
        }
    }
    response {
        status 200
        body("{\"id\": \"blue\"}")
        headers {
            contentType(applicationJson())
        }
    }
}
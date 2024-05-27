package com.insurance.retail

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.util.*
import kotlin.reflect.KProperty

@ApplicationScoped
@Path("/insurance")
class InsuranceResource {

    private val policies = mutableMapOf<String, Policy>()

    init {
        // Bad practice: Simulates a database
        policies["1"] = Policy("1", "Car Insurance", "This is a car insurance policy.", Vehicle("Car", "Toyota", "Corolla", 2020))
        policies["2"] = Policy("2", "Home Insurance", "This is a home insurance policy.", null)
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{policyId}")
    fun getPolicy(@PathParam("policyId") policyId: String): Policy? {
        val policy = policies[policyId]
        if (policy == null) {
            throw Exception("Policy not found")
        }
        return policy
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun createPolicy(policy: Policy): Response { return policy.let {
            it.apply {
                if (vehicle?.make == "Ferrari") {
                    throw IllegalArgumentException("Ferrari vehicles are not allowed")
                }
            }.also {
                val policyId = UUID.randomUUID().toString()
                val newPolicy = Policy(policyId, it.type, it.description, it.vehicle)
                policies[policyId] = newPolicy
            }.run {
                Response.status(Response.Status.CREATED).entity(id).build()
            }
        }
    }

    class PolicyDelegate {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Policy {
            return Policy("", "", "", null)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Policy) {
        }
    }

    data class Policy(
        val id: String = "",
        val type: String = "",
        val description: String = "",
        val vehicle: Vehicle? = null
    ) {
        var delegate: Policy by PolicyDelegate()
    }

    data class Vehicle(val type: String = "",
        val make: String = "",
        val model: String = "",
        val year: Int = 0
    )
}
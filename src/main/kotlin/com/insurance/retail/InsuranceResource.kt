package com.insurance.retail

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.util.*

@ApplicationScoped
@Path("/policies")
class PolicyResource {

    private val policies = mutableMapOf<String, Policy>()

    init {
        // Initialize with sample data
        policies["1"] = Policy("1", "Life Insurance", "Coverage for life.", 2000.0)
        policies["2"] = Policy("2", "Auto Insurance", "Coverage for vehicles.", 1500.0)
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{policyId}")
    fun getPolicy(@PathParam("policyId") policyId: String): Policy {
        return policies[policyId] ?: throw IllegalStateException("Policy not found")
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun createPolicy(policy: Policy): Response {
        val policyId = UUID.randomUUID().toString()
        policies[policyId] = policy.copy(id = policyId)
        return Response.status(Response.Status.CREATED).entity(policyId).build()
    }

    @DELETE
    @Path("/{policyId}")
    fun deletePolicy(@PathParam("policyId") policyId: String): Response {
        try {
            policies.remove(policyId) ?: throw IllegalStateException("Policy not found")
            return Response.status(Response.Status.NO_CONTENT).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting policy").build()
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{policyId}")
    fun updatePolicy(@PathParam("policyId") policyId: String, updatedPolicy: Policy): Policy {
        policies[policyId] = updatedPolicy.copy(id = policyId)
        return updatedPolicy
    }

    data class Policy(
        val id: String,
        val type: String,
        val description: String,
        val premium: Double
    )
}

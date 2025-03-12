package com.insurance.retail

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.util.*

@ApplicationScoped
@Path("/insurance")
class InsurancePolicyResource {

    private val policies = HashMap<String, Policy>()

    init {
        // Initialize with sample data
        policies["1"] = Policy("1", "Life Insurance", "Coverage for life.", 2000.0)
        policies["2"] = Policy("2", "Auto Insurance", "Coverage for vehicles.", 1500.0)
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{policyId}")
    fun getPolicy(@PathParam("policyId") policyId: String): Response {
        val policy = policies[policyId]
        return if (policy != null) {
            Response.ok(policy).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).entity("Policy not found").build()
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun createPolicy(policy: Policy): Response {
        val policyId = UUID.randomUUID().toString()
        if (policy.premium < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Premium cannot be negative").build()
        }
        policies[policyId] = policy.copy(id = policyId)
        return Response.status(Response.Status.CREATED).entity(policyId).build()
    }

    @DELETE
    @Path("/{policyId}")
    fun deletePolicy(@PathParam("policyId") policyId: String): Response {
        return if (policies.remove(policyId) != null) {
            Response.status(Response.Status.NO_CONTENT).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).entity("Policy not found").build()
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{policyId}")
    fun updatePolicy(@PathParam("policyId") policyId: String, updatedPolicy: Policy): Response {
        if (!policies.containsKey(policyId)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Policy not found").build()
        }
        if (updatedPolicy.premium < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Premium cannot be negative").build()
        }
        policies[policyId] = updatedPolicy.copy(id = policyId)
        return Response.ok(updatedPolicy).build()
    }

    data class Policy(
        val id: String,
        val type: String,
        val description: String,
        val premium: Double
    )
}

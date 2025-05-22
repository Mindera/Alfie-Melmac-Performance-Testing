package services

import domain.TestSuite
import domain.dtos.TestSuiteRequestDTO
import domain.dtos.TestSuiteResponseDTO
import repos.IRepos.ITestSuiteRepository
import services.IServices.ITestSuiteService

class TestSuiteService(private val repo: ITestSuiteRepository) : ITestSuiteService {

    override fun create(request: TestSuiteRequestDTO): TestSuiteResponseDTO {
        val suite = TestSuite(name = request.name, description = request.description)
        val id = repo.save(suite)
        return suite.copy(id = id).toResponse()
    }

    override fun listAll(): List<TestSuiteResponseDTO> = repo.findAll().map { it.toResponse() }

    override fun findById(id: Int): TestSuiteResponseDTO? = repo.findById(id)?.toResponse()

    private fun TestSuite.toResponse(): TestSuiteResponseDTO =
            TestSuiteResponseDTO(
                    id = this.id!!,
                    name = this.name,
                    description = this.description,
                    initialTimestamp = this.initialTimestamp?.toString(),
                    endTimestamp = this.endTimestamp?.toString()
            )
}

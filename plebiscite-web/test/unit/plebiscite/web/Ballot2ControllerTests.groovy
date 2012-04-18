package plebiscite.web



import org.junit.*
import grails.test.mixin.*

@TestFor(BallotController)
@Mock(Ballot2)
class Ballot2ControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/ballot2/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.ballot2InstanceList.size() == 0
        assert model.ballot2InstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.ballot2Instance != null
    }

    void testSave() {
        controller.save()

        assert model.ballot2Instance != null
        assert view == '/ballot2/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/ballot2/show/1'
        assert controller.flash.message != null
        assert Ballot2.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/ballot2/list'


        populateValidParams(params)
        def ballot2 = new Ballot2(params)

        assert ballot2.save() != null

        params.id = ballot2.id

        def model = controller.show()

        assert model.ballot2Instance == ballot2
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/ballot2/list'


        populateValidParams(params)
        def ballot2 = new Ballot2(params)

        assert ballot2.save() != null

        params.id = ballot2.id

        def model = controller.edit()

        assert model.ballot2Instance == ballot2
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/ballot2/list'

        response.reset()


        populateValidParams(params)
        def ballot2 = new Ballot2(params)

        assert ballot2.save() != null

        // test invalid parameters in update
        params.id = ballot2.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/ballot2/edit"
        assert model.ballot2Instance != null

        ballot2.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/ballot2/show/$ballot2.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        ballot2.clearErrors()

        populateValidParams(params)
        params.id = ballot2.id
        params.version = -1
        controller.update()

        assert view == "/ballot2/edit"
        assert model.ballot2Instance != null
        assert model.ballot2Instance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/ballot2/list'

        response.reset()

        populateValidParams(params)
        def ballot2 = new Ballot2(params)

        assert ballot2.save() != null
        assert Ballot2.count() == 1

        params.id = ballot2.id

        controller.delete()

        assert Ballot2.count() == 0
        assert Ballot2.get(ballot2.id) == null
        assert response.redirectedUrl == '/ballot2/list'
    }
}

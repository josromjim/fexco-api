angular.module('todoApp', [])
  .controller('TodoListController', ['$scope', '$http', '$templateCache', function($scope, $http, $templateCache) {
    var todoList = this;
    todoList.todos = [];
    todoList.editMode = false;
    todoList.editingTodo = null;
    
    // WebSocket Initialization. We could initialize the websocket bringing the
	// configuration from server, nevertheless, to keep things simple, we do it
	// here
    var taskSocket = new WebSocket("ws://localhost:8080/webclient/tunnel/task");
  
    taskSocket.onmessage = function(message) {
        var message = JSON.parse(message.data);
        
        switch(message.type){
	        case "insert":
	        	todoList.appendLocal(message.data);
	        	break;
	        case "update":
	        	todoList.updateLocal(message.data);
	        	break;
	        case "delete":
	        	todoList.removeLocal(message.data);
	        	break;
        }  

        // We need to refresh the scope
        $scope.$apply();
    }
 
    taskSocket.onclose = function() {
        todoList.setError("Socket error", "An error occured with the WebSocket.");
    }
    
    todoList.getAllTodos = function(){
    	todoList.resetError();
        $http.get('back/tasks').then(
        function(response){
        	todoList.todos = response.data;
        },
        function() {
      	  todoList.setError("Error listing all", "An error occurred while listing all tasks");
        });
    }
    
    todoList.submit = function() {
    	if(todoList.editMode)
    		todoList.updateTodo();
    	else
    		todoList.addTodo();
    }
 
    todoList.addTodo = function() {
    	var todo = {"task":todoList.todoTask};

    	todoList.resetError();
    	$http.post('back/tasks', todo).then(
    	  function(response){
    		  todoList.appendLocal(response.data);
          }, 
    	  function(response) {
        	  todoList.setError("Error adding", "An error occurred while adding a new task");
          }
      	);
    	todoList.todoTask = '';
    }
    
    todoList.updateTodo = function(){
    	var todo = {"id":todoList.editingTodo.id, "task": todoList.todoTask};
    	
    	todoList.resetError();
    	$http.put('back/tasks', todo).then(
          function(response){
        	todoList.editingTodo.task = todo.task; // This is faster than
													// todoList.updateLocal()
													// because
													// we already have the
													// object in memory
        	todoList.resetTodoField();
          },
          function(){
        	todoList.setError('An error occurred while updating the task');
          }
        );
    }
    
    todoList.deleteTodo = function(todo) {    	
    	todoList.resetError();
    	$http.delete('back/tasks/' + todo.id).then(
    	  function(response){
    		  todoList.removeLocal(response.data);
          }, 
    	  function(response) {
        	  todoList.setError('An error occurred while deleting the task');
          }
      	);
    }
    
    todoList.appendLocal = function(todo) {
    	if(!todoList.findTodo(todo)){
    		todoList.todos.push(todo);
    	}
    }
    
    todoList.updateLocal = function(todo) {
    	var t = todoList.findTodo(todo);
    	
    	if(t != false){
    		t.task = todo.task;
    	}
    }
    
    todoList.removeLocal = function(todo) {    	
		let index = todoList.todos.findIndex(t => t.id==todo.id);
		if (index > -1) {
			todoList.todos.splice(index, 1);
		}
    }
    
    todoList.findTodo = function(todo) {
    	let found = todoList.todos.find(t => t.id==todo.id);
    	
    	return found == undefined ? false : found;
    }
    
    todoList.editTodo = function(todo){
    	todoList.resetError();
    	todoList.todoTask = todo.task;
    	todoList.editingTodo = todo;
    	todoList.editMode = true;
    }
    
    todoList.resetTodoField = function() {
    	todoList.resetError();
    	todoList.todoTask = '';
    	todoList.editMode = false;
    	todoList.editingTodo = null;
    };
 
    todoList.resetError = function() {
    	todoList.error = false;
    	todoList.errorMessage = '';
    }

    todoList.setError = function(short, message) {
    	todoList.error = true;
    	todoList.errorMessage = {"short": short, "long": message}
    }
    
    todoList.getAllTodos();

  }]);
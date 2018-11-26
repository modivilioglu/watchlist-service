## Synopsis
This is a Scala Project, a Restful Web Service, implemented with Akka HTTP.
The aim of this project is to add, query and delete Contents from the
logged in Customers' WatchLists for Now TV.

## Code Structure
The code is separated in 3 main parts
- Server
- Service
- Dao

Server Layer is responsible for routing, and handling of the Request and Response
Service Layer is responsible for the logical boundary and 
Dao Layer is responsible for storage

There are 3 Domain Models - Content, Customer and CustomerWatchList

On Server Layer there is a Server model, called Contents, used to encapsulate Content List in POST request.

## How to Run

```
cmd > git clone https://github.com/modivilioglu/watchlist-service.git
cmd > cd watchlist-service
cmd > sbt run
```
Once the server is up, you can run the following commands:

##### GET 
```
GET /api/v1/watchlist/CS1 HTTP/1.1
   Host: CS1:p4ssw0rd@localhost:8080
   cache-control: no-cache
   ``` 
returns
```
{
       "watchList": {
           "list": [
               {
                   "id": "CONT8"
               },
               {
                   "id": "CONT9"
               },
               {
                   "id": "CONT1"
               }
           ],
           "customer": {
               "id": "CS1"
           }
       }
   }
   ```   
##### POST
```$xslt
POST /api/v1/watchlist HTTP/1.1
Host: CS1:p4ssw0rd@localhost:8080
Content-Type: application/json
cache-control: no-cache
{"contents": [{
                "id": "CONT1"
            },
            {
            	"id": "CONT9"
            }]}
```
returns
```$xslt
{
    "watchList": {
        "list": [
            {
                "id": "CONT8"
            },
            {
                "id": "CONT9"
            },
            {
                "id": "CONT1"
            }
        ],
        "customer": {
            "id": "CS1"
        }
    }
}

```

##### DELETE
```$xslt

DELETE /api/v1/watchlist?ids=CONT1,CONT5 HTTP/1.1
Host: CS1:p4ssw0rd@localhost:8080
cache-control: no-cache
```

returns

```{
       "watchList": {
           "list": [
               {
                   "id": "CONT8"
               },
               {
                   "id": "CONT9"
               }
           ],
           "customer": {
               "id": "CS1"
           }
       }
   }
   ```
## Data
Data is kept in memory, in a simple Map and List format.
Unfortunately that is the only place mutable variable was used.
To provide total immutability, also an Akka Actor could have been used,
passing data in between states during context.become, but to keep things simple
that part is for now neglected.

There are 3 main data
Customer Data, which is used for Authentication
and Watchlist Data, which holds the CustomerWatchlist along with
Content.

## Development Approach
The following approached has been followed during the development:

Since WatchlistServiceApi represents the interface for use cases, on the WatchlistServiceApiSpec test class
- A. Test cases on the service class have been generated according to the "acceptance criteria", which is mentioned on the spec.
- B. Additional test scenarios in the spec was added as new test scenarios
and 1 - 1 implemented
- C. After each implementation, the test cases are run to make sure that all pass
- D. When all test cases have passed, final refactorings have been made.

## Dependency Injection
For dependency injection, guice library is selected which is also used in Play Framework.
The Injection is used to decouple the InMemoryDao from the Service layer, as later we might want to switch / add from
reading from the Memory, to reading from a database or some other stream, so implementation is highly likely to change.

## Technical Spec
Implicit conversions and classes are implemented to

- give extra behaviour to the existing class without modifying the code
- to introduce |-| and |+| operations on CustomerWatchList case class in order to 
simply show the logic of CRUD operations, and minimize the complexity on list operations
on DAO code layer.   
- provide a neater way to parse comma separated IDs in tests into Content Lists

For example: 
```$xslt
val newWatchList = watchListOfCustomer |-| contents

```
enables a good understanding instead of 
```
val newList = customerWatchList.list.filter(content => !contents.contains(content))
CustomerWatchList(newList, customerWatchList.customer)
```
This way the one who reads the code can simply symbolically understand the logic,
rather than the implementation details.

Likewise |+| operation is also introduced on the implicit class. For operations,
it uses Monoid[List[Content]] for safely combining two Lists.
```$xslt

def |+| (contents: List[Content]) : CustomerWatchList = {
      val newList = Monoid[List[Content]].combine(customerWatchList.list, contents).distinct
      CustomerWatchList(newList, customerWatchList.customer)
}
```

A CustomerWatchList specific Monoid, where 2 watchlists are combined together could also
be created. However, the Api input represents always a Content List, so the operations are addition or subtraction
of Contents to and from CustomerWatchList.

## Akka HTTP and JSON responses
There are 2 nice features that Akka HTTP provides
- ExceptionHandler
- RejectionHandler

By overwriting your custom handlers you can marshall your RestResponses into JSON format.
By that you guarantee the JSON responses in any case. Also for special cases, you can
return special and more accurate HTTP Response Codes.


##  Further Enhancements
Enabling new Dao classes are easy via Dependency injection. 
Also further approach maybe adding more robustness via usage of Either for error handling, taking a different approach on both cases. Xor in cats library is highly recommended on its right bias behaviour as well as its flatmap function.

The functions may return Future values to enable asynchronous behaviour.
Furthermore, since the Futures are Eagerly generated side effects in a sense, using of Cats IO monads 
may enable flatmapping on different side effects and materializing the async code in a specific point.

As mentioned, the implicit conversion of String line "CNT123, CNT456, CNT789" to Contents Server Model
enables parsing the text data easily, but also makes it possible to read a lot of data from text, creating Contents classes, for test cases. 
An automated test can read from human/machine generated text file.

Also for performance tests Gatling Tests should be added.

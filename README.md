# Общие сведения

Данный репозиторий содержит **два раздела**: `REST API` и `Android Application`. 
 - Раздел `REST API` содержит реализацию *серверной части приложения*, разработанного в рамках дипломной работы. Для его создания использовался язык программирования `Java`, фреймворк `Java Spring Framework` и среда разработки `IntelliJ IDEA Community Edition`.
 - Раздел `Android Application` содержит реализацию *клиентской части приложения*, разработанного в рамках дипломной работы. Для его создания использовался язык программирования `Kotlin` и среда разработки `Android Studio`.

В качестве СУБД использовалась `Microsoft SQL Server`.
 
# REST API

Проект `REST API` содержит следующие разделы:

 - `configs` — раздел с классами, настраивающими конфигурацию проекта; 
 - `contollers` — раздел с классами-контроллерами, обрабатывающими HTTP-запросы;
 - `exceptions` — раздел с персонализированными классами-исключениями (на данный момент здесь только 1 класс `RequestError`, необходимый для обработки случаев возникновения ошибок при выполнении HTTP-запроса);
 - `models` — раздел с классами, являющимися либо сущностями базы данных, либо телами HTTP-запросов;
 - `repositories` — раздел с репозиториями, соответствующими сущностям базы данных;
 - `services` — раздел с классами-сервисами (на данный момент содержит только 1 класс `AccountService`);
 - `tsp` — раздел с алгоритмами решения задачи коммивояжера;
 - `utils` — раздел со вспомогательными классами проекта (на данный момент содержит только 1 класс `JwtTokenUtils`, позволяющий создавать токены).

Точка запуска проекта — класс `ManagementServerApplication`.

## HTTP-запросы, обрабатываемые REST API

| Контроллер | Путь | HTTP-метод | Описание |
|--|--|--|--|
| `AuthController` | `/auth/login` | `POST` | Запрос аутентификации пользователя. В теле запроса должны содержаться логин и пароль пользователя. |
|  | `/auth/register` | `POST` | Создание нового пользователя и аккаунта для него на основе данных из тела запроса: ФИО, номера телефона, логина, пароля. Номер телефона и логин не должны быть заняты другими пользователями.
|  | `/auth/refresh` | `POST` | Обновление токенов пользователя на основе `refresh` токена, указанного в заголовке запроса.
| `UserController` | `/users/` `update/{id}` | `POST` | Обновление данных пользователя с кодом `id` на основе тех, что указаны в теле запроса. |
| `Account` `Controller` | `/accounts/`  `updateLogin/{id}` | `POST` | Обновление логина аккаунта пользователя с кодом `id` на основе указанного в теле запроса. |
| | `/accounts/`  `checkPassword/{id}` | `POST` | Проверка пароля пользователя с кодом аккаунта `id` на корректность. |
| | `/accounts/`  `updatePassword/{id}` | `POST` | Обновление пароля аккаунта пользователя с кодом `id` на основе указанного в теле запроса. |
| | `/accounts/getUser` | `GET` | Получение данных пользователя и его аккаунта на основе `access` токена, указанного в заголовке запроса. |
| `DeliveryPoint` `Controller` | `/points/add` | `POST` | Создание новой точки доставки на основе данных, указанных в теле запроса. |
| | `/points/get` | `GET` | Получение списка всех точек доставки из базы данных. |
| `UserRoute` `Controller` | `/userRoute/add` | `POST` | Создание нового маршрута (решения задачи коммивояжера) пользователя на основе данных из тела запроса. |
|  | `/userRoute/` `get/{id}` | `GET` | Получение списка всех маршрутов пользователя с кодом `id` из базы данных. |
|  | `/userRoute/` `delete/{id}` | `POST` | Удаление маршрута по его `id`.|
| `TspController` | `/getSolution` | `POST` | Получение нового маршрута (решения задачи коммивояжера) на основе опции, стартовой вершины, набора вершин, участвующих в маршруте и матрицы расстояний, указанных в теле запроса. |

# Android Application

Проект `Android Application` содержит следующие разделы:

 - `activities` — раздел с классами, отвечающими за логику работы страниц мобильного приложения;
 - `adapters` — раздел с классами-адаптерами, настраивающими работу сложных списков (например, отображение решения задачи коммивояжера за счет класса `AdapterRouteInfo`);
 - `api` — раздел с интерфейсами для взаимодействия с реализованным REST API и сторонним сервисом [Openrouteservice](https://openrouteservice.org/) для построения матрицы расстояний между точками, участвующими в построении маршрута. В этой директории содержаться подразделы `models` с классами, соответствующими телам HTTP-запросов и -ответов, а также `utils` со вспомогательными классами для поддержания кэшируемости REST API (данные классы позволяют хранить данные запросов без необходимости повторного вызова);
 - `data` — раздел с классами, являющимися сущностями базы данных;
 - `dialogs` — раздел с классами, реализующими диалоговые окна в мобильном приложении;
 - `fragments` — раздел с разделами главного меню приложения: новый маршрут (класс `NewRouteFragment`), история маршрутов (класс `RouteHistoryFragment`) и настройки аккаунта (класс `AccountFragment`);
 - `utils` — раздел со вспомогательными классами проекта.

define({ "api": [
  {
    "type": "post",
    "url": "/login",
    "title": "Log into the app",
    "name": "Login",
    "group": "Authorization",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "email",
            "description": "<p>The email of the user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "password",
            "description": "<p>The password of the user</p>"
          }
        ]
      }
    },
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "error",
            "description": "<p>The error field has a string with an exact error</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": " HTTP/1.1 200 OK\n{\n  \"email\": \"6209be52@mail.com\",\n  \"password\": \"fa2568a8dd82c24a6ee22df3f19d642d\"\n}",
          "type": "json"
        }
      ]
    },
    "sampleRequest": [
      {
        "url": "/api/login"
      }
    ],
    "version": "0.0.0",
    "filename": "api/login_api.js",
    "groupTitle": "Authorization"
  },
  {
    "type": "get",
    "url": "/refresh",
    "title": "Refresh your JWT token",
    "name": "RefreshToken",
    "group": "Authorization",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "jwt",
            "description": "<p>The JWT you have currently</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "refresh",
            "description": "<p>The refresh token you were given at signup</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "token",
            "description": "<p>A JWT token that can be used to authenticate futher API calls</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": " HTTP/1.1 200 OK\n{\n  \"jwt\": Encrypted_JWT_Token,\n  \"refresh\": RefreshToken\n}",
          "type": "json"
        }
      ]
    },
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "error",
            "description": "<p>The error field has a string with an exact error</p>"
          }
        ]
      }
    },
    "sampleRequest": [
      {
        "url": "/api/refresh"
      }
    ],
    "version": "0.0.0",
    "filename": "api/refresh_api.js",
    "groupTitle": "Authorization"
  },
  {
    "type": "delete",
    "url": "/refresh",
    "title": "Delete (revoke) a refresh token",
    "name": "RefreshToken_Delete",
    "group": "Authorization",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "jwt",
            "description": "<p>The JWT you have currently</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "refresh",
            "description": "<p>The refresh token you want revoked</p>"
          }
        ]
      }
    },
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "error",
            "description": "<p>The error field has a string with an exact error</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": " HTTP/1.1 200 OK\n{\n  \"jwt\": Encrypted_JWT_Token,\n  \"refresh\": RefreshToken\n}",
          "type": "json"
        }
      ]
    },
    "sampleRequest": [
      {
        "url": "/api/refresh"
      }
    ],
    "version": "0.0.0",
    "filename": "api/refresh_api.js",
    "groupTitle": "Authorization"
  },
  {
    "type": "post",
    "url": "/register",
    "title": "Register a user account",
    "name": "RegisterUser",
    "group": "Authorization",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "nickname",
            "description": "<p>The nickname of the user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "fname",
            "description": "<p>The first name of the user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "lname",
            "description": "<p>The last name of the user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "gender",
            "description": "<p>The gender of the user (F/M/O)</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "dob",
            "description": "<p>The date of birth of the user (DD/MM/YYYY)</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "email",
            "description": "<p>The email of the user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "password",
            "description": "<p>The password of the user</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "token",
            "description": "<p>A JWT token that can be used to authenticate futher API calls</p>"
          },
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "user_id",
            "description": "<p>The id assigned to the user for unique identification</p>"
          },
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "refresh",
            "description": "<p>A refresh token that can be used to generate JWTs throught the API</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": " HTTP/1.1 200 OK\n{\n  \"nickname\": \"abode\",\n  \"fname\": \"Abode\",\n  \"lname\": \"Saafan\",\n  \"gender\": \"M\",\n  \"dob\": \"25/03/1996\",\n  \"email\": \"abodesaafan@hotmail.com\",\n  \"password\": \"password123\"\n}",
          "type": "json"
        }
      ]
    },
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "error",
            "description": "<p>The error field has a string with an exact error</p>"
          }
        ]
      }
    },
    "sampleRequest": [
      {
        "url": "/api/register"
      }
    ],
    "version": "0.0.0",
    "filename": "api/register_api.js",
    "groupTitle": "Authorization"
  },
  {
    "type": "post",
    "url": "/games",
    "title": "Create a new game",
    "name": "CreateGame",
    "group": "Games",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "name",
            "description": "<p>The name of the game you are creating</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "type",
            "description": "<p>The type of the game you are creating (Serious, casual, ..)</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "skill",
            "description": "<p>The intended skill range for this game (x/10) (next week)</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "total_players",
            "description": "<p>The total required players for the game</p>"
          },
          {
            "group": "Parameter",
            "type": "date_time",
            "optional": false,
            "field": "start_time",
            "description": "<p>The time the game starts</p>"
          },
          {
            "group": "Parameter",
            "type": "time",
            "optional": false,
            "field": "duration",
            "description": "<p>The duration of the game</p>"
          },
          {
            "group": "Parameter",
            "type": "location",
            "optional": false,
            "field": "location",
            "description": "<p>The location of the game represented in location object (x/y)</p>"
          },
          {
            "group": "Parameter",
            "type": "location_notes",
            "optional": false,
            "field": "string",
            "description": "<p>how to get into the court</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "description",
            "description": "<p>Short description for the game (less than 250 characters)</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "gender",
            "description": "<p>The preferred for the game (if any)</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "age",
            "description": "<p>The preferred age range for the game (if any)</p>"
          },
          {
            "group": "Parameter",
            "type": "list",
            "optional": false,
            "field": "enforced_params",
            "description": "<p>List of parmeters that the creator wants to enforce</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "gameId",
            "description": "<p>The id of the game that has been created</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "     HTTP/1.1 200 OK\n    {\n      \"name\": \"abode's game\",\n      \"type\": \"casual\",\n      \"intended_skill\": \"5\",\n      \"total_players_required\": 6,\n      \"start_time\": \"25/03/2018 14:30:00 PM EST\",\n      \"duration\": \"01h30m00s\",\n      \"location\": {x: 500, y:500},\n\t\t\"location_notes\": \"Come around the back and knock on the blue door\"\n      \"description\": \"Casual basketball game\",\n      \"gender\": \"A\",\n      \"age\": \"20 +-3\",\n      \"enforced_params\": [skill, gender, age]\n    }",
          "type": "json"
        }
      ]
    },
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "error",
            "description": "<p>The error field has a string with an exact error</p>"
          }
        ]
      }
    },
    "sampleRequest": [
      {
        "url": "/api/games"
      }
    ],
    "version": "0.0.0",
    "filename": "api/games_api.js",
    "groupTitle": "Games"
  }
] });

// Register
var invalidUsername = "Username: Must be alphanumerical, lowercase and 4 to 10 characters and can include '_'.";
var uniqueUsernameError = "Username: Someone else has that username.";
var invalidPassword = "Password: Must be alphanumerical, can include '? ! ~ $ # % *', and must be between 6 to 80 characters.";
var invalidFirstName = "First name: Must be Alphabetical.";
var invalidLastName = "Last name: Must be Alphabetical.";
var invalidGender = "Gender: It can be specified as M/m, F/f, O/o.";
var invalidDob = "Date of Birth: Dates must be entered in as MM/DD/YYYY.";
var invalidEmail = "Email: Must be in a valid format.";
var uniqueEmailError = "Email: Someone else has that email.";
var registerFailError = "Register: Failed to register user into the database.";
var userIdFail = "Register: Failed to retrieve user id.";
var createRefreshFail = "Register: Unable to create refresh token.";
// Others
var usersFail = "Failed to retrieve users.";
var wrongEmailPassword = "Username or password is not valid.";
var emailNotRegistered = "Invalid email. Email not registered.";
var noRefreshToken = "No refresh token has been found for you, please attempt to login to acquire a new token.";
var invalidJwt = "The JWT token you have provided is invalid.";
var invalidGameName = "The game name you have entered is invalid.";
var invalidGameType = "The game type you have entered is invalid.";
var invalidGameDuration = "The game duration has to be specified as time in seconds.";
var invalidGameTotalPlayers = "The total players required for a game must be specified as an integer between 2 and 100.";
var invalidGameGenderPreference = "The gender preference you specified is not valid.";
var invalidGameStartTime = "The game start time you specified is not valid.";
var invalidGameAgeRange = "The age range you specified is not valid.";
var invalidGameLocation = "The game location you specified is not valid.";
var invalidLocationNotes = "The location notes description you specified is not valid.";
var invalidGameDescription = "The game description you specified is not valid.";
var invalidEnforcedParamList = "The enforced parameters list you have specified is not valid.";
var invalidGameCreation = "Error creating the game.";
var invalidGameSkillOffset = "The game skill offset you have entered is invalid.";
var invalidGameSkill = "The game skill value you have entered is invalid.";
var invalidGameScheduleConflict = "The game you are trying to create can not be created because the time conflicts with another game you have created.";
var invalidGame = "The game you specified does not exist.";
var problemWithGameCreation = "The game you tried to create had some errors. Game created but user could not join in own game.";
var loginError = "Username or password is not valid.";
var invalidSearchObject = "The search object is invalid. You must specify 'game' or 'user' as a search object.";
var invalidGameId = "The game ID you entered is invalid. Game ID's are natural numbers.";
var invalidGameLocationRange = "The location range must be within 1 and 500 KMs.";
var invalidUserId = "The user ID you entered is invalid. User ID's are natural numbers.";
var invalidRating = "The rating you entered is invalid. Ratings are natural numbers.";
var invalidReviewTag = "The review tag you entered is invalid. Review tags are natural numbers.";
var invalidLeaveGame = "Failed to leave game specified. Please make sure you are a player in the game.";
var gamerNotAdded = "Failed to add user to the game.";
var cannotJoinGame = "Lack necessary requirements for the user to join the game.";
var invalidLFriendInvite = "Failed to send a friend invite.";
var AcceptFriendFailed = "Failed to accept the friend request.";
var InvalidFriendRequest = "The friend request does not exist.";
var DeleteFriendFailed = "Failed to delete this request or remove this friend.";
var UpdateFailed = "Failed to update your extended profile.";
var BlockFriendFailed = "Failed to block this user.";
var FriendRequestExists = "The friend request already exists.";
var checkFriendEntryValidationForBlock = "Entry validation for Block Failed.";
var ListFriendFailed = "List all friends have failed.";
var ListBlockUserRequestFailed = "List all blocked users have failed.";
var emptySearchResults = "Nothing matches your search.";
var listFriendRequestFailed = "Listing all friend requests has failed.";
var deleteFailed = "Delete account failed.";
var refreshDoesNotExist = "The refresh token you want to delete does not exist.";
var refreshMissingParams = "Please provide your current JWT and the refresh token you want revoked.";
var updateUserFailed = "Update user details failed";
var updatePassword = "Update user password failed";

module.exports = {
	invalidUsername,
	uniqueUsernameError,
	invalidPassword,
	invalidFirstName,
	invalidLastName,
	invalidGender,
	invalidDob,
	invalidEmail,
	uniqueEmailError,
	emailNotRegistered,
	registerFailError,
	userIdFail,
	createRefreshFail,
	wrongEmailPassword,
	noRefreshToken,
	invalidJwt,
	invalidGameName,
	invalidGameType,
	invalidGameDuration,
	invalidGameTotalPlayers,
	invalidGameGenderPreference,
	invalidGameStartTime,
	invalidGameAgeRange,
	invalidGameLocation,
	invalidLocationNotes,
	invalidGameDescription,
	invalidEnforcedParamList,
	invalidGameCreation,
	invalidGameSkillOffset,
	invalidGameSkill,
	invalidGameScheduleConflict,
	invalidGame,
	problemWithGameCreation,
	loginError,
	invalidSearchObject,
	invalidGameId,
	invalidGameLocationRange,
	invalidUserId,
	invalidRating,
	invalidReviewTag,
	usersFail,
	invalidLeaveGame,
	gamerNotAdded,
	cannotJoinGame,
	invalidLFriendInvite,
	AcceptFriendFailed,
	InvalidFriendRequest,
	DeleteFriendFailed,
	UpdateFailed,
	BlockFriendFailed,
	FriendRequestExists,
	checkFriendEntryValidationForBlock,
	ListFriendFailed,
	ListBlockUserRequestFailed,
	emptySearchResults,
	listFriendRequestFailed,
	deleteFailed,
	refreshMissingParams,
	refreshDoesNotExist,
	updateUserFailed,
	updatePassword
};

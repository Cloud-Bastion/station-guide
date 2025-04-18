package dev.aventix.station.authserver.user

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service


@Service
class OAuth2StationUserService(
    private val repo: UserRepository,
): DefaultOAuth2UserService() {

//    @PostConstruct
//    fun init() {
//        val entity = Account().apply {
//            this.usernameP = "one"
//            this.passwordP = "{noop}pw"
//        }
//        repo.saveAndFlush(entity)
//    }

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        // 1. Delegate to the default implementation to get user details from Google
        val oauth2User = super.loadUser(userRequest)

        // 2. Extract necessary attributes from the OAuth2User
        val registrationId = userRequest.clientRegistration.registrationId // "google"
        val googleSubjectId = oauth2User.name // Standard way to get unique ID (sub claim)
        val email = oauth2User.getAttribute<String>("email")
        val name = oauth2User.getAttribute<String>("name")
        val picture = oauth2User.getAttribute<String>("picture")

        // Add other attributes as needed...
        println("Fetching OAuth2User with subject id: $googleSubjectId")
        println("OAuth2 User Attributes from " + registrationId + ": " + oauth2User.attributes)

        // 3. Find or create a local user based on Google info (Example Logic)
        // User localUser = userRepository.findByGoogleId(googleSubjectId)
        //     .map(user -> {
        //         // User exists, potentially update details
        //         System.out.println("Found existing user by Google ID: " + user.getEmail());
        //         user.setName(name); // Example update
        //         user.setPictureUrl(picture);
        //         return userRepository.save(user);
        //     })
        //     .orElseGet(() -> {
        //         // User doesn't exist, create a new one
        //         // Check if email exists from another login method? Handle carefully.
        //         System.out.println("Creating new user for email: " + email);
        //         User newUser = new User();
        //         newUser.setGoogleId(googleSubjectId);
        //         newUser.setEmail(email); // Ensure email is unique in your DB
        //         newUser.setName(name);
        //         newUser.setProvider(registrationId); // Store the provider ('google')
        //         newUser.setPictureUrl(picture);
        //         // Set default roles/authorities, enabled status, etc.
        //         // newUser.setRoles(Set.of(roleRepository.findByName("ROLE_USER").get()));
        //         newUser.setEnabled(true);
        //         return userRepository.save(newUser);
        //     });


        // --- Placeholder for DB logic ---
        println("Simulating find/create for Google User: $email")


        // In a real app, load/create `localUser` from DB here
        // --- End Placeholder ---


        // 4. Create a custom principal object representing YOUR local user
        // This object should ideally implement UserDetails (for Spring Security)
        // and potentially OAuth2User (to hold original attributes if needed).
        // It must contain the authorities/roles defined in YOUR system.

        // Example using a placeholder principal (replace with your actual implementation)
        // return new LocalAppUser(localUser.getId(), localUser.getEmail(), localUser.getPassword(),
        //                         localUser.isEnabled(), /* account non-expired etc.*/,
        //                         mapRolesToAuthorities(localUser.getRoles()), // Your local roles!
        //                         oauth2User.getAttributes()); // Optionally pass original attributes


        // --- Return the original Google user for now until DB logic is added ---
        // This means the Principal in SecurityContext will be the default OAuth2User
        // and tokens issued by your AS might reflect Google claims initially.
        // You'll need Step 4 (Token Customizer) later if you want local claims.
        println("Returning default OAuth2User principal for now.")
        return oauth2User // TODO: Replace with your custom local principal later
        // --- End Temporary Return ---
    }

    // Helper method (if your principal needs GrantedAuthority)
    // private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
    //    return roles.stream()
    //               .map(role -> new SimpleGrantedAuthority(role.getName()))
    //               .collect(Collectors.toList());
    // }


}
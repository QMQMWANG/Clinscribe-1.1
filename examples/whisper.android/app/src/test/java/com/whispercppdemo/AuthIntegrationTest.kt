package com.whispercppdemo.ui.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.robolectric.annotation.Config
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.CollectionReference
import org.robolectric.shadows.ShadowLooper
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AuthIntegrationTest {

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    @Mock
    private lateinit var mockAuthResult: AuthResult

    @Mock
    private lateinit var mockFirebaseUser: FirebaseUser

    @Mock
    private lateinit var mockTask: Task<AuthResult>

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mockAuth = mock(FirebaseAuth::class.java)
        mockFirestore = mock(FirebaseFirestore::class.java)
        authViewModel = AuthViewModel(auth = mockAuth, db = mockFirestore)
    }



    @Test
    fun `test offline login`() {
        var isSuccess = false
        authViewModel.login(email = "test@t.com", password = "111111", {
            isSuccess = true
        }, {
            isSuccess = false
        })

        assertTrue(isSuccess)
        verify(mockAuth, never()).signInWithEmailAndPassword(anyString(), anyString())
    }

    @Test
    fun `test login exception handling`() {
        `when`(mockAuth.signInWithEmailAndPassword(anyString(), anyString()))
            .thenReturn(Tasks.forException(Exception("Network error")))

        var isSuccess = false
        authViewModel.login(email = "test@example.com", password = "password123", {
            isSuccess = true
        }, {
            isSuccess = false
        })

        assertFalse(isSuccess)
    }

    @Test
    fun `test login success`() {
        `when`(mockTask.isSuccessful).thenReturn(true)
        `when`(mockAuth.signInWithEmailAndPassword(anyString(), anyString()))
            .thenReturn(Tasks.forResult(mockAuthResult))

        var isSuccess = false
        authViewModel.login(email = "test@example.com", password = "password123", {
            isSuccess = true
        }, {
            isSuccess = false
        })

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        assertTrue(isSuccess)
    }

    @Test
    fun `test register success`() {
        // Mock the behavior for a successful registration
        `when`(mockTask.isSuccessful).thenReturn(true)
        `when`(mockAuth.createUserWithEmailAndPassword(anyString(), anyString()))
            .thenReturn(Tasks.forResult(mockAuthResult))
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.uid).thenReturn("userId")

        // Mock Firestore interaction
        val mockDocumentReference: DocumentReference = mock(DocumentReference::class.java)
        val mockCollectionReference: CollectionReference = mock(CollectionReference::class.java)

        `when`(mockFirestore.collection(anyString())).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

        var isSuccess = false
        authViewModel.register(email = "newuser@example.com", password = "strongpassword123", {
            isSuccess = true
        }, {
            isSuccess = false
        })

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        assertTrue(isSuccess)
    }

    @Test
    fun `test reset password success`() {
        val mockResetTask: Task<Void> = Tasks.forResult(null)

        `when`(mockAuth.sendPasswordResetEmail(anyString()))
            .thenReturn(mockResetTask)

        var isSuccess = false
        authViewModel.resetPassword(email = "test@example.com", {
            isSuccess = true
        }, {
            isSuccess = false
        })

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        assertTrue(isSuccess)
    }

    @Test
    fun `test reset password failure`() {
        val mockResetTask: Task<Void> = Tasks.forException(Exception("Reset password failed"))

        `when`(mockAuth.sendPasswordResetEmail(anyString()))
            .thenReturn(mockResetTask)

        var isSuccess = false
        authViewModel.resetPassword(email = "test@example.com", {
            isSuccess = true
        }, {
            isSuccess = false
        })

        assertFalse(isSuccess)
    }
}

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TaskComment} from "../../models/comment";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {UserService} from "../../services/user.service";
import {User} from "../../models/user";
import {AuthService} from "../../services/auth.service";
import Swal from "sweetalert2";
import {CommentService} from "../../services/comment.service";

@Component({
  selector: 'app-comment',
  standalone: true,
  imports: [
    NgForOf,
    DatePipe,
    NgIf
  ],
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.css'
})
export class CommentComponent implements OnInit {
  constructor(private readonly userService: UserService,
              private readonly authService: AuthService,
              private readonly commentService: CommentService) {
  }
  @Input({required: true}) comment!: TaskComment;
  @Output() commentDeleted = new EventEmitter<number>();

  commentAuthor: string = '';
  currentUserId!: number;

  ngOnInit(): void {
    this.userService.getUserById(this.comment.authorId).subscribe(res => {
      let user = res.data as User;
      this.commentAuthor = user.username;
      this.currentUserId = this.authService.getUserFromJwt().id;
    });
  }

  editComment(comment: TaskComment) {
    Swal.fire({
      title: 'Edit Comment',
      input: 'textarea',
      inputLabel: 'Comment',
      inputValue: comment.content,
      showCancelButton: true,
      confirmButtonText: 'Save',
      cancelButtonText: 'Cancel',
      inputAttributes: {
        'aria-label': 'Type your comment here'
      },
      preConfirm: (newText) => {
        if (!newText.trim()) {
          Swal.showValidationMessage('Comment cannot be empty');
        }
        return newText;
      }
    }).then((result) => {
      if (result.isConfirmed && result.value.trim()) {
        comment.content = result.value.trim();
        console.log(comment)
        // Optional: If you want to also save to backend immediately:
        this.commentService.updateComment(comment).subscribe({
          next: (res) => {
            this.comment = res.data as TaskComment

            Swal.fire('Saved!', 'Your comment has been updated.', 'success');
          },
          error: (err) => {
            let message = err.error.message;
            Swal.fire('Error!', message, 'error');
          }
        });
      }
    });
  }

  deleteComment(comment: TaskComment) {
    Swal.fire({
      title: 'Are you sure?',
      text: 'This action cannot be undone!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.commentService.deleteComment(comment.id, comment.taskId).subscribe({
          next: () => {

            Swal.fire(
              'Deleted!',
              'Your comment has been deleted.',
              'success'
            );

            this.commentDeleted.emit(this.comment.id);
          },
          error: (err) => {
            let message = err.error.message;
            Swal.fire('Error!', message, 'error');
          }
        });
      }
    });
  }
}

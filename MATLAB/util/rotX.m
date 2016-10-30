function R = rotX(alpha)


    R = diag( [ 1 cos(alpha) cos(alpha) 1 ] );
    R(2,3) = -sin(alpha);
    R(3,2) = sin(alpha);


end
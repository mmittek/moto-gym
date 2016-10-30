function R = rotY(beta)

    R = diag([ cos(beta) 1 cos(beta) 1 ]);
    R(1,3) = sin(beta);
    R(3,1) = -sin(beta);

end
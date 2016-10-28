function [R4D] = R3DtoR4D(R3D)
    R4D = zeros(4,4);
    R4D(4,4) = 1;
    R4D(1:3,1:3) = R3D;

end